package com.projects.job_tracker.application.observatory;

import java.util.*;
import org.springframework.stereotype.Service;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;

/** No transaction spans browser/network work; evidence commits before parsing/import failures. */
@Service
public class RunCollectionService {
    private final CaptureService captures;
    private final CollectionControlService control;
    private final CaptureImportService importer;
    private final MarketCollector collector;
    private final ActiveCollections active;
    public static final String VERSION="OBSERVATORY_BROWSER_V1";
    public RunCollectionService(CaptureService captures,CollectionControlService control,CaptureImportService importer,MarketCollector collector,ActiveCollections active) {
        this.captures=captures; this.control=control; this.importer=importer; this.collector=collector; this.active=active;
    }
    public String preview(UUID protocolId,int revision) {
        var protocol=captures.protocolHistory(protocolId).stream().filter(p -> p.revision()==revision).findFirst().orElseThrow(() -> new IllegalArgumentException("Versión inexistente"));
        return collector.searchUrl(protocol);
    }
    public CaptureRun collect(UUID runId) {
        var run=captures.run(runId);
        var protocol=captures.protocolHistory(run.protocolId()).stream().filter(p -> p.revision()==run.protocolRevision()).findFirst().orElseThrow();
        String url=collector.searchUrl(protocol);
        String version=collector.version();
        if (!control.claim(runId,url,version)) return captures.run(runId);
        active.start(runId);
        try { return collectClaimed(runId,protocol,url,version); }
        finally { active.stop(runId); }
    }
    private CaptureRun collectClaimed(UUID runId,CaptureModels.ProtocolVersion protocol,String url,String version) {
        boolean executionFailed=false;
        MarketCollector.Report report;
        try {
            report=collector.collect(protocol,item -> {
                var raw=control.stage(runId,item,version);
                if (!raw.status().equals("PENDING")) {
                    if (captures.run(runId).state()!=RunState.RUNNING) throw new IllegalStateException("Recolección interrumpida");
                    return;
                }
                try {
                    if (item.parsed()==null) throw new IllegalArgumentException("No se pudo identificar el anuncio en la tarjeta");
                    var job=item.parsed();
                    if (!job.platform().source().equalsIgnoreCase(protocol.spec().source())) throw new IllegalArgumentException("El parser devolvió otra fuente");
                    var data=new PostingData(job.title(),job.companyName(),job.location(),job.requirements(),job.salaryMin(),job.salaryMax(),null,null,null);
                    importer.automatic(runId,new CaptureImportService.Input(item.itemKey(),job.url(),job.externalId(),item.originalText(),item.observedAt(),data),raw.id());
                } catch (RuntimeException ex) { control.failed(raw.id(),"Importación pendiente de revisión: "+ex.getMessage()); }
            },()->control.attempt(runId).filter(a -> a.status().equals("RUNNING")).isPresent(),page->control.page(runId,page,version));
        } catch (RuntimeException ex) {
            executionFailed=true;
            report=new MarketCollector.Report(url,version,0,false,"El portal o navegador no completó la consulta ("+ex.getClass().getSimpleName()+"). No se interpreta como cero vacantes.");
        }
        if (captures.run(runId).state()!=RunState.RUNNING) return captures.run(runId);
        var items=control.items(runId);
        long imported=items.stream().filter(i -> i.status().equals("IMPORTED")).count();
        boolean truncated=report.truncated() || captures.observations(runId).stream().filter(o -> o.availability()==Availability.AVAILABLE).count()>=protocol.spec().resultLimit();
        String notes=report.notes()+" Elementos conservados: "+items.size()+"; importados: "+imported+"; con error: "+items.stream().filter(i -> i.status().equals("ERROR")).count()+". Paginación limitada por protocolo; cobertura total del mercado desconocida.";
        var state=imported>0 || truncated ? RunState.PARTIAL : RunState.FAILED;
        String kind=imported>0 ? "WITH_EVIDENCE" : executionFailed ? "EXECUTION_FAILURE" : items.isEmpty() ? "NO_CONFIRMED_RESULTS" : "IMPORT_ERRORS";
        CaptureRun result;
        try { result=control.complete(runId,new CaptureService.Finish(state,Math.max(report.pagesVisited(),Math.max(control.pages(runId).size(),items.isEmpty() ? 0 : 1)),truncated,notes),kind); }
        catch (RuntimeException ex) { if (captures.run(runId).state()!=RunState.RUNNING) return captures.run(runId); throw ex; }
        return result;
    }
}
