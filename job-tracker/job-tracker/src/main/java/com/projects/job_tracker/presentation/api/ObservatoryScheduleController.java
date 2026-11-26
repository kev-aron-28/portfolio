package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projects.job_tracker.application.observatory.ObservatoryScheduleService;
import com.projects.job_tracker.domain.observatory.ScheduleModels.*;
import com.projects.job_tracker.infrastructure.observatory.automation.ObservatoryAutomationSettings;

@RestController
@RequestMapping("/api/observatory")
public class ObservatoryScheduleController {
    private final ObservatoryScheduleService service;
    private final ObservatoryAutomationSettings settings;
    private final boolean automationEnabled;
    public ObservatoryScheduleController(ObservatoryScheduleService service,ObservatoryAutomationSettings settings,@Value("${observatory.automation.enabled:true}") boolean automationEnabled) {
        this.service=service; this.settings=settings; this.automationEnabled=automationEnabled;
    }
    @GetMapping("/automation") public Map<String,Object> automation() { return Map.of("enabled",automationEnabled,"schedulingEnabled",settings.schedulingEnabled,"workers",settings.workers,"leaseSeconds",settings.leaseSeconds,"maxRuntimeSeconds",settings.maxRuntimeSeconds); }
    @GetMapping("/schedules") public List<Schedule> list() { return service.list(); }
    @GetMapping("/schedules/{id}") public Schedule get(@PathVariable UUID id) { return service.get(id); }
    @GetMapping("/schedules/{id}/history") public List<Version> history(@PathVariable UUID id) { return service.history(id); }
    @GetMapping("/schedules/{id}/tasks") public List<Task> tasks(@PathVariable UUID id,@RequestParam(defaultValue="25") int limit,@RequestParam(defaultValue="0") int offset) { return service.tasks(id,limit,offset); }
    @PostMapping("/schedules") public ResponseEntity<Schedule> create(@RequestBody ObservatoryScheduleService.Edit edit) {
        var schedule=service.save(null,edit);
        return ResponseEntity.created(URI.create("/api/observatory/schedules/"+schedule.version().id())).body(schedule);
    }
    @PutMapping("/schedules/{id}") public Schedule edit(@PathVariable UUID id,@RequestBody ObservatoryScheduleService.Edit edit) { return service.save(id,edit); }
}
