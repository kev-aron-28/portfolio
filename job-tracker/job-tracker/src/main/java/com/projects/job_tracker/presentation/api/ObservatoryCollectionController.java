package com.projects.job_tracker.presentation.api;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import com.projects.job_tracker.application.observatory.*;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;

@RestController
@RequestMapping("/api/observatory")
public class ObservatoryCollectionController {
    private final RunCollectionService collection;
    private final CollectionControlService control;
    private final CaptureImportService importer;
    private final CaptureService captures;
    public ObservatoryCollectionController(RunCollectionService collection,CollectionControlService control,CaptureImportService importer,CaptureService captures) { this.collection=collection; this.control=control; this.importer=importer; this.captures=captures; }
    @GetMapping("/protocols/{id}/collection-preview") public Map<String,String> preview(@PathVariable UUID id,@RequestParam int revision) { return Map.of("searchUrl",collection.preview(id,revision),"coverage","BOUNDED_EXPLORATORY"); }
    public record Interruption(String reason) {}
    @PostMapping("/runs/{id}/collection/interrupt") public CaptureRun interrupt(@PathVariable UUID id,@RequestBody Interruption input) { return control.interrupt(id,input.reason()); }
    @PostMapping("/runs/{id}/collect") public CaptureRun collect(@PathVariable UUID id) { return collection.collect(id); }
    @GetMapping("/runs/{id}/collection") public Map<String,Object> attempt(@PathVariable UUID id) { captures.run(id); return Map.of("attempt",control.attempt(id).<Object>map(a -> a).orElse(Map.of()),"items",control.items(id),"pages",control.pages(id)); }
    @PostMapping("/runs/{id}/items") public Observation manual(@PathVariable UUID id,@RequestBody CaptureImportService.Input input) { return importer.manual(id,input); }
}
