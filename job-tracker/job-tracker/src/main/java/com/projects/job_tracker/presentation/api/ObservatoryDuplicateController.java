package com.projects.job_tracker.presentation.api;

import java.time.Instant;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import com.projects.job_tracker.application.observatory.DuplicateService;
import com.projects.job_tracker.domain.observatory.DuplicateModels.*;

@RestController
@RequestMapping("/api/observatory")
public class ObservatoryDuplicateController {
    private final DuplicateService service;
    public ObservatoryDuplicateController(DuplicateService service) { this.service=service; }
    @GetMapping("/postings/{id}/identity") public Identity identity(@PathVariable UUID id,@RequestParam(required=false) Instant at) { return service.identity(id,at); }
    @GetMapping("/duplicate-candidates") public List<Candidate> candidates(@RequestParam(defaultValue="25") int limit,@RequestParam(defaultValue="0") int offset) { return service.candidates(limit,offset); }
    @GetMapping("/duplicate-groups") public List<Group> groups() { return service.groups(); }
    @GetMapping("/duplicate-reviews") public List<ReviewView> reviews() { return service.reviews(); }
    @GetMapping("/duplicate-reviews/{id}/history") public List<Review> history(@PathVariable UUID id) { return service.history(id); }
    @PostMapping("/duplicate-reviews") public Review decide(@RequestBody DuplicateService.Decide command) { return service.decide(command); }
}
