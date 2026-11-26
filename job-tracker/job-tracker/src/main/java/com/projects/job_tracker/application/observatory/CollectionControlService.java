package com.projects.job_tracker.application.observatory;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;

@Service
@Transactional
public class CollectionControlService {
    private final CollectionLedger ledger;
    private final CaptureRepository captures;
    private final CaptureService captureService;
    public CollectionControlService(CollectionLedger ledger,CaptureRepository captures,CaptureService captureService) { this.ledger=ledger; this.captures=captures; this.captureService=captureService; }
    public CaptureRun complete(UUID runId,CaptureService.Finish command,String kind) {
        var result=captureService.finishCollected(runId,command);
        ledger.finish(runId,command.notes(),kind);
        return result;
    }
    public boolean claim(UUID runId,String url,String version) {
        var run=captures.lockRun(runId);
        if (ledger.attempt(runId).isPresent()) return false;
        if (run.state()!=RunState.RUNNING || run.observationCount()>0) throw new IllegalArgumentException("La recolección requiere una ejecución nueva y vacía");
        ledger.begin(runId,url,version); return true;
    }
    public CollectionLedger.RawItem stage(UUID runId,MarketCollector.Item item,String version) {
        var run=captures.lockRun(runId);
        var raw=ledger.stage(runId,item,version);
        if (run.state()==RunState.RUNNING) ledger.heartbeat(runId,java.time.Instant.now());
        if (run.state()!=RunState.RUNNING && raw.status().equals("PENDING")) {
            ledger.failed(raw.id(),"La ejecución se interrumpió antes de importar; se conserva evidencia recibida después del cierre");
            return ledger.items(runId).stream().filter(i -> i.id().equals(raw.id())).findFirst().orElseThrow();
        }
        return raw;
    }
    public void page(UUID runId,MarketCollector.PageEvidence page,String version) {
        var run=captures.lockRun(runId);
        ledger.page(runId,page,version);
        if (run.state()==RunState.RUNNING) ledger.heartbeat(runId,java.time.Instant.now());
    }
    @Transactional(readOnly=true) public List<MarketCollector.PageEvidence> pages(UUID id) { return ledger.pages(id); }
    public void failed(UUID item,String issue) { ledger.failed(item,issue); }
    public void finish(UUID runId,String notes) { ledger.finish(runId,notes); }
    public void finish(UUID runId,String notes,String kind) { ledger.finish(runId,notes,kind); }
    public void heartbeat(UUID runId,java.time.Instant now) { ledger.heartbeat(runId,now); }
    public List<UUID> stale(java.time.Instant heartbeatBefore,java.time.Instant startedBefore) { return ledger.staleAttempts(heartbeatBefore,startedBefore); }
    public void recoverIfExpired(UUID runId,java.time.Instant now,long leaseSeconds,long maxRuntimeSeconds) {
        captures.lockRun(runId);
        var attempt=ledger.lockAttempt(runId).orElse(null);
        if (attempt==null || !attempt.status().equals("RUNNING")) return;
        boolean timeout=!attempt.startedAt().isAfter(now.minusSeconds(maxRuntimeSeconds));
        if (timeout || !attempt.heartbeatAt().isAfter(now.minusSeconds(leaseSeconds)))
            interruptInternal(runId,timeout ? "Tiempo máximo de captura agotado" : "Intento abandonado: señal de actividad vencida",timeout ? "TIME_LIMIT" : "LEASE_EXPIRED");
    }
    public CaptureRun interrupt(UUID runId,String reason) { return interruptInternal(runId,reason,"USER_INTERRUPTED"); }
    public CaptureRun recoverForced(UUID id,String reason,String kind) { return interruptInternal(id,reason,kind); }
    private CaptureRun interruptInternal(UUID runId,String reason,String kind) {
        reason=CaptureModels.required(reason,"Motivo de interrupción",1000);
        var run=captures.lockRun(runId);
        if (ledger.attempt(runId).isEmpty()) throw new IllegalArgumentException("La ejecución no pertenece al recolector");
        if (run.state()!=RunState.RUNNING) {
            ledger.finish(runId,run.notes()==null ? reason : run.notes(),"RECOVERED_FINAL_STATE");
            return run;
        }
        long available=captures.runObservations(runId).stream().filter(o -> o.availability()==Availability.AVAILABLE).count();
        var protocol=captures.protocolHistory(run.protocolId()).stream().filter(p -> p.revision()==run.protocolRevision()).findFirst().orElseThrow();
        String notes="Recolección interrumpida: "+reason+". Cobertura no confirmada; no representa cero vacantes.";
        var closed=new CaptureRun(run.id(),run.protocolId(),run.protocolRevision(),run.requestKey(),run.startedAt(),java.time.Instant.now(),
            available>0 ? RunState.PARTIAL : RunState.FAILED,ledger.pages(runId).isEmpty() ? null : ledger.pages(runId).size(),available>=protocol.spec().resultLimit(),notes,run.observationCount());
        for (var raw:ledger.items(runId)) if (raw.status().equals("PENDING")) ledger.failed(raw.id(),"Captura interrumpida antes de importar; evidencia conservada");
        captures.finish(closed); ledger.finish(runId,notes,kind); return closed;
    }
    @Transactional(readOnly=true) public List<CollectionLedger.RawItem> items(UUID id) { return ledger.items(id); }
    @Transactional(readOnly=true) public Optional<CollectionLedger.Attempt> attempt(UUID id) { return ledger.attempt(id); }
}
