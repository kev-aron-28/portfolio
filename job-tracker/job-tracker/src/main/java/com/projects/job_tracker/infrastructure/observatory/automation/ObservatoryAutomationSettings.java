package com.projects.job_tracker.infrastructure.observatory.automation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ObservatoryAutomationSettings {
    public final long leaseSeconds,maxRuntimeSeconds,pollMillis;
    public final int workers;
    public final boolean schedulingEnabled;
    public ObservatoryAutomationSettings(@Value("${observatory.automation.lease-seconds:90}") long leaseSeconds,
            @Value("${observatory.automation.max-runtime-seconds:1800}") long maxRuntimeSeconds,
            @Value("${observatory.automation.workers:1}") int workers,
            @Value("${observatory.scheduling.enabled:true}") boolean schedulingEnabled,
            @Value("${observatory.automation.poll-ms:10000}") long pollMillis) {
        if (leaseSeconds<30 || maxRuntimeSeconds<leaseSeconds || workers<1 || workers>4 || pollMillis<1000 || pollMillis>leaseSeconds*1000/3)
            throw new IllegalArgumentException("Observatorio: lease >= 30 segundos, tiempo máximo >= lease y 1–4 trabajadores");
        this.pollMillis=pollMillis; this.leaseSeconds=leaseSeconds; this.maxRuntimeSeconds=maxRuntimeSeconds; this.workers=workers; this.schedulingEnabled=schedulingEnabled;
    }
}
