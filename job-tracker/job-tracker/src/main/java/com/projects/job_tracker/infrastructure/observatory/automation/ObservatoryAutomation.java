package com.projects.job_tracker.infrastructure.observatory.automation;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;
import com.projects.job_tracker.application.observatory.*;

@Component
@ConditionalOnProperty(name="observatory.automation.enabled",havingValue="true",matchIfMissing=true)
public class ObservatoryAutomation {
    private static final Logger log=LoggerFactory.getLogger(ObservatoryAutomation.class);
    private final ObservatoryMaintenanceService maintenance;
    private final ObservatoryScheduleService schedules;
    private final ObservatoryScheduleWorker worker;
    private final ObservatoryAutomationSettings settings;
    private final ExecutorService executor;
    private final ScheduledExecutorService timer;
    private boolean started;
    private final Semaphore slots;
    private final Map<UUID,UUID> local=new ConcurrentHashMap<>();
    public ObservatoryAutomation(ObservatoryMaintenanceService maintenance,ObservatoryScheduleService schedules,ObservatoryScheduleWorker worker,ObservatoryAutomationSettings settings) {
        this.maintenance=maintenance; this.schedules=schedules; this.worker=worker; this.settings=settings;
        slots=new Semaphore(settings.workers);
        timer=Executors.newSingleThreadScheduledExecutor(action -> { var thread=new Thread(action,"observatory-maintenance"); thread.setDaemon(true); return thread; });
        executor=Executors.newFixedThreadPool(settings.workers,action -> { var thread=new Thread(action,"observatory-collector"); thread.setDaemon(true); return thread; });
    }
    @EventListener(ApplicationReadyEvent.class)
    public synchronized void start() {
        if (!started) { started=true; timer.scheduleWithFixedDelay(this::poll,settings.pollMillis,settings.pollMillis,TimeUnit.MILLISECONDS); }
    }

    public void poll() {
        Instant now=Instant.now();
        try {
            for (var entry:local.entrySet()) schedules.heartbeat(entry.getKey(),entry.getValue(),now);
            maintenance.maintain(now);
            if (!settings.schedulingEnabled) return;
            while (slots.tryAcquire()) {
                Optional<com.projects.job_tracker.domain.observatory.ScheduleModels.Task> due;
                try { due=schedules.claimDue(now); } catch(RuntimeException ex) { slots.release(); throw ex; }
                if (due.isEmpty()) { slots.release(); break; }
                var task=due.get(); local.put(task.id(),task.leaseToken());
                try {
                    executor.execute(() -> {
                        try { worker.execute(task.id(),task.leaseToken()); }
                        catch(RuntimeException ex) { log.warn("Observatory task {} stopped; durable recovery will reconcile it",task.id(),ex); }
                        finally { local.remove(task.id()); slots.release(); }
                    });
                } catch(RejectedExecutionException ex) { local.remove(task.id()); slots.release(); log.warn("Observatory executor stopped; task {} remains recoverable",task.id()); break; }
            }
        } catch(RuntimeException ex) { log.warn("Observatory maintenance/dispatch failed; will retry next poll",ex); }
    }
    @PreDestroy public void close() { timer.shutdownNow(); executor.shutdownNow(); }
}
