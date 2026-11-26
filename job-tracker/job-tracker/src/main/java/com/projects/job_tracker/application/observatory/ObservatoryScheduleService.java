package com.projects.job_tracker.application.observatory;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.ScheduleModels.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.RunState;
import com.projects.job_tracker.infrastructure.observatory.automation.ObservatoryAutomationSettings;

@Service
@Transactional
public class ObservatoryScheduleService {
    private final ObservatoryScheduleRepository repository;
    private final CaptureService captures;
    private final CollectionControlService control;
    private final ObservatoryAutomationSettings settings;
    private final MarketCollector collector;
    public ObservatoryScheduleService(ObservatoryScheduleRepository repository,CaptureService captures,CollectionControlService control,ObservatoryAutomationSettings settings,MarketCollector collector) {
        this.repository=repository; this.captures=captures; this.control=control; this.settings=settings; this.collector=collector;
    }
    public record Edit(Integer expectedRevision,UUID requestKey,Config config,String reason) {}
    public Schedule save(UUID id,Edit edit) {
        if (edit==null || edit.config()==null) throw new IllegalArgumentException("Indica configuración de programación");
        String reason=CaptureModels.required(edit.reason(),"Motivo",4000);
        repository.lockRegistry();
        String hash=EvidenceHash.sha256("OBS_SCHEDULE_CREATE_V1|"+edit.config()+"|"+reason);
        if (id==null) {
            if (edit.requestKey()==null) throw new IllegalArgumentException("Indica una clave de alta");
            var prior=repository.byRequestKey(edit.requestKey());
            if (prior.isPresent()) {
                if (!prior.get().hash().equals(hash)) throw new IllegalArgumentException("La clave de alta ya se utilizó con otros datos");
                return prior.get().schedule();
            }
        }
        var protocol=captures.protocolHistory(edit.config().protocolId()).stream().filter(p -> p.revision()==edit.config().protocolRevision()).findFirst().orElseThrow(() -> new IllegalArgumentException("Versión de protocolo inexistente"));
        if (protocol.spec().collectionPlan()==null) throw new IllegalArgumentException("La programación requiere un protocolo con plan ejecutable");
        collector.searchUrl(protocol);
        Schedule prior=id==null ? null : get(id);
        if (prior!=null && !Objects.equals(edit.expectedRevision(),prior.version().revision())) throw new IllegalArgumentException("La programación cambió; recarga antes de editar");
        if (prior!=null && prior.version().revision()==Integer.MAX_VALUE) throw new IllegalArgumentException("Límite de revisiones alcanzado");
        UUID key=id==null ? UUID.randomUUID() : id;
        boolean sameCalendar=prior!=null && prior.version().config().protocolId().equals(edit.config().protocolId())
            && prior.version().config().protocolRevision().equals(edit.config().protocolRevision()) && prior.version().config().startAt().equals(edit.config().startAt());
        repository.save(new Version(key,prior==null ? 1 : prior.version().revision()+1,edit.config(),protocol.spec().intervalMinutes(),reason,micros(Instant.now())),edit.requestKey(),hash,sameCalendar ? prior.nextRunAt() : edit.config().startAt());
        if (prior!=null) {
            for (Task pending:repository.pending(id)) closePending(pending,Instant.now(),"Configuración editada o pausada");
            if (prior.activeTaskId()!=null) {
                var assigned=repository.task(prior.activeTaskId(),true).orElseThrow();
                if (assigned.state()==TaskState.CLAIMED && assigned.runId()==null) {
                    closePending(assigned,Instant.now(),"Configuración editada o pausada antes de iniciar captura");
                    repository.release(id,assigned.id(),null,0);
                }
            }
        }
        return get(key);
    }
    @Transactional(readOnly=true) public List<Schedule> list() { return repository.schedules(); }
    @Transactional(readOnly=true) public Schedule get(UUID id) { return repository.schedule(id).orElseThrow(() -> new ResourceNotFoundException("Programación del observatorio no encontrada")); }
    @Transactional(readOnly=true) public List<Version> history(UUID id) { get(id); return repository.history(id); }
    @Transactional(readOnly=true) public List<Task> tasks(UUID id,int limit,int offset) {
        get(id); if (limit<1 || limit>100 || offset<0) throw new IllegalArgumentException("Paginación no válida (1–100)");
        return repository.tasks(id,limit,offset);
    }
    public Optional<Task> claimDue(Instant now) {
        now=micros(now); repository.lockRegistry();
        var due=repository.due(now); if (due.isEmpty()) return Optional.empty();
        var schedule=due.getFirst(); Version version=schedule.version(); var config=version.config();
        Instant cutoff=now;
        Task pending=repository.pending(version.id()).stream().filter(t -> !t.dueAt().isAfter(cutoff)).findFirst().orElse(null);
        Instant next=schedule.nextRunAt(); Task task;
        if (!next.isAfter(now)) {
            for (var retry:repository.pending(version.id())) closePending(retry,now,"Omitido: llegó el siguiente intervalo regular");
            long interval=version.intervalMinutes()*60L,skipped=Duration.between(next,now).getSeconds()/interval;
            Instant slot=next.plusSeconds(Math.multiplyExact(skipped,interval)); next=slot.plusSeconds(interval);
            task=new Task(UUID.randomUUID(),version.id(),version.revision(),slot,slot,0,null,TaskState.CLAIMED,UUID.randomUUID(),now.plusSeconds(settings.leaseSeconds),now,null,null,null,skipped);
            repository.insert(task);
        } else {
            if (pending==null) return Optional.empty();
            task=copy(pending,TaskState.CLAIMED,UUID.randomUUID(),now.plusSeconds(settings.leaseSeconds),now,null,null,pending.notes()); repository.update(task);
        }
        repository.activate(version.id(),task.id(),next); return Optional.of(task);
    }
    public Optional<Task> bind(UUID id,UUID token,Instant now) {
        repository.lockRegistry(); var task=repository.task(id,true).orElseThrow();
        if (!owns(task,token) || !task.leaseUntil().isAfter(now)) return Optional.empty();
        if (task.runId()!=null) return Optional.of(task);
        var current=get(task.scheduleId());
        if (!current.version().config().enabled() || current.version().revision()!=task.scheduleRevision()) {
            closePending(task,now,"Configuración cambió antes de iniciar la captura");
            repository.release(task.scheduleId(),task.id(),null,current.consecutiveFailures()); return Optional.empty();
        }
        var version=repository.history(task.scheduleId()).stream().filter(v -> v.revision()==task.scheduleRevision()).findFirst().orElseThrow();
        var run=captures.start(version.config().protocolId(),new CaptureService.Start(version.config().protocolRevision(),task.id()));
        Task bound=copy(task,TaskState.RUNNING,token,now.plusSeconds(settings.leaseSeconds),task.claimedAt(),run.id(),null,task.notes()); repository.update(bound); return Optional.of(bound);
    }
    public void complete(UUID id,UUID token,Instant now) {
        repository.lockRegistry(); var task=repository.task(id,true).orElseThrow();
        if (!owns(task,token)) return;
        if (task.runId()==null || captures.run(task.runId()).state()==RunState.RUNNING) return;
        finalizeTask(task,now,false);
    }
    public void heartbeat(UUID id,UUID token,Instant now) { repository.heartbeat(id,token,now.plusSeconds(settings.leaseSeconds),now.minusSeconds(settings.maxRuntimeSeconds)); }
    public void recover(Instant now) {
        repository.lockRegistry();
        for (Task snapshot:repository.activeTasks()) {
            Task task=repository.task(snapshot.id(),true).orElseThrow();
            if (task.runId()!=null && captures.run(task.runId()).state()!=RunState.RUNNING) { finalizeTask(task,now,false); continue; }
            boolean hard=!task.claimedAt().isAfter(now.minusSeconds(settings.maxRuntimeSeconds));
            if (!hard && task.leaseUntil().isAfter(now)) continue;
            if (task.runId()!=null) {
                var attempt=control.attempt(task.runId());
                if (!hard && attempt.isPresent() && attempt.get().status().equals("RUNNING") && attempt.get().heartbeatAt().isAfter(now.minusSeconds(settings.leaseSeconds))) {
                    repository.update(copy(task,task.state(),task.leaseToken(),now.plusSeconds(settings.leaseSeconds),task.claimedAt(),task.runId(),null,task.notes())); continue;
                }
                if (attempt.isPresent()) control.recoverIfExpired(task.runId(),now,settings.leaseSeconds,settings.maxRuntimeSeconds);
                if (captures.run(task.runId()).state()==RunState.RUNNING) {
                    if (attempt.isPresent()) control.recoverForced(task.runId(),"Tiempo de tarea agotado","TIME_LIMIT");
                    else {
                        long available=captures.observations(task.runId()).stream().filter(o -> o.availability()==CaptureModels.Availability.AVAILABLE).count();
                        var pinned=repository.history(task.scheduleId()).stream().filter(v -> v.revision()==task.scheduleRevision()).findFirst().orElseThrow();
                        var protocol=captures.protocolHistory(pinned.config().protocolId()).stream().filter(v -> v.revision()==pinned.config().protocolRevision()).findFirst().orElseThrow();
                        captures.finishCollected(task.runId(),new CaptureService.Finish(available>0 ? RunState.PARTIAL : RunState.FAILED,0,available>=protocol.spec().resultLimit(),"La tarea se abandonó antes de iniciar el navegador; se conserva la bitácora"));
                    }
                }
            }
            finalizeTask(task,now,true);
        }
    }
    private void finalizeTask(Task task,Instant now,boolean abandoned) {
        var run=task.runId()==null ? null : captures.run(task.runId());
        var attempt=task.runId()==null ? Optional.<CollectionLedger.Attempt>empty() : control.attempt(task.runId());
        if (run!=null && attempt.isPresent() && attempt.get().status().equals("RUNNING") && run.state()!=RunState.RUNNING) {
            control.recoverForced(run.id(),"Se recuperó el cierre final de la captura","RECOVERED_FINAL_STATE"); attempt=control.attempt(run.id());
        }
        String kind=attempt.map(CollectionLedger.Attempt::terminationKind).orElse("EXECUTION_FAILURE");
        boolean evidence=run!=null && captures.observations(run.id()).stream().anyMatch(o -> o.availability()==CaptureModels.Availability.AVAILABLE);
        boolean userCancelled="USER_INTERRUPTED".equals(kind);
        boolean recovered="LEASE_EXPIRED".equals(kind) || "TIME_LIMIT".equals(kind);
        TaskState state=userCancelled ? TaskState.CANCELLED : evidence ? TaskState.SUCCEEDED : abandoned || recovered ? TaskState.ABANDONED : TaskState.FAILED;
        String notes=run==null ? "Tarea abandonada antes de vincular captura" : run.notes();
        repository.update(copy(task,state,task.leaseToken(),task.leaseUntil(),task.claimedAt(),task.runId(),micros(now),notes));
        Schedule schedule=get(task.scheduleId());
        if (!Objects.equals(schedule.activeTaskId(),task.id())) return;
        int failures=schedule.version().revision()!=task.scheduleRevision() ? schedule.consecutiveFailures() : evidence ? 0 : userCancelled ? schedule.consecutiveFailures() : schedule.consecutiveFailures()+1;
        repository.release(task.scheduleId(),task.id(),task.runId(),failures);
        var version=schedule.version();
        boolean retryable=!evidence && !userCancelled && !"IMPORT_ERRORS".equals(kind);
        if (retryable && version.config().enabled() && version.revision()==task.scheduleRevision() && task.retryNumber()<version.config().maxRetries()) {
            long delay=Math.min(1440L,version.config().backoffMinutes()*(1L<<task.retryNumber()));
            Instant retryAt=micros(now.plusSeconds(delay*60));
            if (retryAt.isBefore(schedule.nextRunAt())) repository.insert(new Task(UUID.randomUUID(),task.scheduleId(),task.scheduleRevision(),task.scheduledFor(),retryAt,task.retryNumber()+1,task.id(),TaskState.PENDING,null,null,null,null,null,"Reintento con espera; evidencia anterior conservada",0));
        }
    }
    private boolean owns(Task task,UUID token) { return token!=null && token.equals(task.leaseToken()) && (task.state()==TaskState.CLAIMED || task.state()==TaskState.RUNNING); }
    private void closePending(Task t,Instant now,String reason) { repository.update(copy(t,TaskState.CANCELLED,t.leaseToken(),t.leaseUntil(),t.claimedAt(),t.runId(),micros(now),reason)); }
    private Task copy(Task t,TaskState state,UUID token,Instant leaseUntil,Instant claimedAt,UUID run,Instant finished,String notes) {
        return new Task(t.id(),t.scheduleId(),t.scheduleRevision(),t.scheduledFor(),t.dueAt(),t.retryNumber(),t.retryOf(),state,token,leaseUntil,claimedAt,run,finished,notes,t.skippedSlots());
    }
    private static Instant micros(Instant now) { return now.truncatedTo(java.time.temporal.ChronoUnit.MICROS); }
}
