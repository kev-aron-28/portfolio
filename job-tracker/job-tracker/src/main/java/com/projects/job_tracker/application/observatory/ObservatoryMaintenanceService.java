package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.projects.job_tracker.infrastructure.observatory.automation.ObservatoryAutomationSettings;

@Service
public class ObservatoryMaintenanceService {
    private final ActiveCollections active;
    private final CollectionControlService control;
    private final ObservatoryScheduleService schedules;
    private final ObservatoryAutomationSettings settings;
    public ObservatoryMaintenanceService(ActiveCollections active,CollectionControlService control,ObservatoryScheduleService schedules,ObservatoryAutomationSettings settings) {
        this.active=active; this.control=control; this.schedules=schedules; this.settings=settings;
    }
    public void maintain(Instant now) {
        for (var entry:active.snapshot().entrySet()) {
            if (!entry.getValue().isAfter(now.minusSeconds(settings.maxRuntimeSeconds))) {
                control.recoverForced(entry.getKey(),"Tiempo máximo de captura agotado","TIME_LIMIT"); active.stop(entry.getKey());
            } else control.heartbeat(entry.getKey(),now);
        }
        for (var id:control.stale(now.minusSeconds(settings.leaseSeconds),now.minusSeconds(settings.maxRuntimeSeconds)))
            control.recoverIfExpired(id,now,settings.leaseSeconds,settings.maxRuntimeSeconds);
        schedules.recover(now);
    }
}
