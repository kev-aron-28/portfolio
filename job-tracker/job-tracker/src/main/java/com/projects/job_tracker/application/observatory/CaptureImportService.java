package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;

@Service
@Transactional
public class CaptureImportService {
    private final CaptureRepository captures;
    private final CaptureService service;
    private final MarketPostingRepository postings;
    private final DuplicateService identities;
    private final SkillCatalogService catalog;
    private final CollectionLedger ledger;
    public CaptureImportService(CaptureRepository captures,CaptureService service,MarketPostingRepository postings,DuplicateService identities,SkillCatalogService catalog,CollectionLedger ledger) {
        this.captures=captures; this.service=service; this.postings=postings; this.identities=identities; this.catalog=catalog; this.ledger=ledger;
    }
    public record Input(String itemKey,String url,String externalId,String originalText,Instant observedAt,PostingData data) {}
    public Observation manual(UUID runId,Input input) {
        return ingest(runId,input,false);
    }
    public Observation automatic(UUID runId,Input input,UUID rawItemId) {
        var result=ingest(runId,input,true);
        ledger.imported(rawItemId,result.postingId(),result.id());
        return result;
    }
    private Observation ingest(UUID runId,Input input,boolean automatic) {
        if (input==null || input.data()==null || input.observedAt()==null || input.originalText()==null || input.originalText().isBlank())
            throw new IllegalArgumentException("Incluye la evidencia, fecha y datos del anuncio");
        String key=CaptureModels.required(input.itemKey(),"Clave del elemento",200);
        String fingerprint=EvidenceHash.sha256((automatic ? "AUTO_IMPORT_V1|" : "MANUAL_IMPORT_V1|")+input);
        var run=captures.lockRun(runId);
        if (!automatic && ledger.attempt(runId).isPresent()) throw new IllegalArgumentException("Esta ejecución pertenece al recolector; utiliza otra para altas manuales");
        var replay=captures.findItem(runId,key,fingerprint);
        if (replay.isPresent()) return replay.get();
        if (run.state()!=RunState.RUNNING) throw new IllegalArgumentException("La ejecución terminó");
        if (input.observedAt().isBefore(run.startedAt()) || input.observedAt().isAfter(Instant.now())) throw new IllegalArgumentException("La fecha debe estar dentro de la ejecución");
        var protocol=service.protocolHistory(run.protocolId()).stream().filter(p -> p.revision()==run.protocolRevision()).findFirst().orElseThrow();
        long available=captures.runObservations(runId).stream().filter(o -> o.availability()==Availability.AVAILABLE).count();
        if (available>=protocol.spec().resultLimit()) throw new IllegalArgumentException("Se alcanzó el límite del protocolo");
        SourceIdentityKey.url(input.url()); SourceIdentityKey.externalId(input.externalId());
        MarketPostingService.validateContext(input.data(),input.originalText(),input.observedAt());
        catalog.validate(input.data(),input.originalText());
        String source=protocol.spec().source();
        var matches=identities.resolveExisting(source,input.url(),input.externalId());
        if (matches.size()>1) throw new IllegalArgumentException("Identidad ambigua: revisa los registros coincidentes antes de importar");
        String method=automatic ? "AUTO_CAPTURE_V1" : "MANUAL_CAPTURE_V1";
        if (!matches.isEmpty()) {
            postings.lock(matches.getFirst()); var existing=postings.find(matches.getFirst()).orElseThrow();
            PostingData data=automatic && existing.evidenceForCurrentRevision().originalText().equals(input.originalText()) ? null : input.data();
            return service.observeWithFingerprint(runId,new CaptureService.Observe(key,existing.id(),existing.current().number(),input.observedAt(),Availability.AVAILABLE,input.originalText(),data),fingerprint,method,input.url(),true);
        }
        UUID id=UUID.randomUUID(); Instant now=Instant.now();
        var observation=new Observation(id,id,runId,key,source,input.url(),input.originalText(),EvidenceHash.sha256(input.originalText()),input.observedAt(),now,Availability.AVAILABLE,1);
        var posting=new MarketPosting(id,source,input.url(),input.originalText(),input.observedAt(),now,
            List.of(new MarketPosting.Revision(1,now,"Alta dentro de captura "+runId,method,input.data(),id)));
        postings.insertCaptured(posting,observation,fingerprint); identities.attach(id,source,input.url(),input.externalId(),null);
        return observation;
    }
}
