package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projects.job_tracker.application.observatory.CaptureService;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;

@RestController
@RequestMapping("/api/observatory")
public class ObservatoryCaptureController {
    private final CaptureService service;
    public ObservatoryCaptureController(CaptureService service) { this.service = service; }
    @GetMapping("/segments") public List<SegmentVersion> segments() { return service.segments(); }
    @GetMapping("/segments/{id}/history") public List<SegmentVersion> segmentHistory(@PathVariable UUID id) { return service.segmentHistory(id); }
    @PostMapping("/segments") public ResponseEntity<SegmentVersion> createSegment(@RequestBody CaptureService.SegmentEdit edit) {
        var result = service.saveSegment(null, edit);
        return ResponseEntity.created(URI.create("/api/observatory/segments/" + result.id() + "/history")).body(result);
    }
    @PutMapping("/segments/{id}") public SegmentVersion reviseSegment(@PathVariable UUID id, @RequestBody CaptureService.SegmentEdit edit) { return service.saveSegment(id, edit); }
    @GetMapping("/protocols") public List<ProtocolVersion> protocols() { return service.protocols(); }
    @GetMapping("/protocols/{id}/history") public List<ProtocolVersion> protocolHistory(@PathVariable UUID id) { return service.protocolHistory(id); }
    @PostMapping("/protocols") public ResponseEntity<ProtocolVersion> createProtocol(@RequestBody CaptureService.ProtocolEdit edit) {
        var result = service.saveProtocol(null, edit);
        return ResponseEntity.created(URI.create("/api/observatory/protocols/" + result.id() + "/history")).body(result);
    }
    @PutMapping("/protocols/{id}") public ProtocolVersion reviseProtocol(@PathVariable UUID id, @RequestBody CaptureService.ProtocolEdit edit) { return service.saveProtocol(id, edit); }
    @PostMapping("/protocols/{id}/runs") public CaptureRun start(@PathVariable UUID id, @RequestBody CaptureService.Start command) { return service.start(id, command); }
    @GetMapping("/runs") public List<CaptureRun> runs(@RequestParam(defaultValue="25") int limit, @RequestParam(defaultValue="0") int offset) { return service.runs(limit, offset); }
    @GetMapping("/runs/{id}") public CaptureRun run(@PathVariable UUID id) { return service.run(id); }
    @PutMapping("/runs/{id}/finish") public CaptureRun finish(@PathVariable UUID id, @RequestBody CaptureService.Finish command) { return service.finish(id, command); }
    @GetMapping("/runs/{id}/observations") public List<Observation> observations(@PathVariable UUID id) { return service.observations(id); }
    @PostMapping("/runs/{id}/observations") public Observation observe(@PathVariable UUID id, @RequestBody CaptureService.Observe command) { return service.observe(id, command); }
}
