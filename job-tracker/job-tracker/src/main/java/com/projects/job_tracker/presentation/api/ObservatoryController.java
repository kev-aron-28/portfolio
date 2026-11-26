package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projects.job_tracker.application.observatory.MarketPostingService;
import com.projects.job_tracker.domain.observatory.MarketPosting;

@RestController
@RequestMapping("/api/observatory/postings")
public class ObservatoryController {
    private final MarketPostingService service;
    public ObservatoryController(MarketPostingService service) { this.service = service; }
    @GetMapping
    public List<MarketPosting> list(@RequestParam(defaultValue = "25") int limit, @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }
    @GetMapping("/{id}")
    public MarketPosting get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping
    public ResponseEntity<MarketPosting> register(@RequestBody MarketPostingService.Registration request) {
        var result = service.register(request);
        return ResponseEntity.created(URI.create("/api/observatory/postings/" + result.id())).body(result);
    }
    @PutMapping("/{id}")
    public MarketPosting correct(@PathVariable UUID id, @RequestBody MarketPostingService.Correction request) {
        return service.correct(id, request);
    }
}
