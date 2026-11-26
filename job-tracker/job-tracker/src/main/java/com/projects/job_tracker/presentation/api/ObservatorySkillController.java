package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projects.job_tracker.application.observatory.SkillCatalogService;
import com.projects.job_tracker.domain.observatory.SkillVersion;

@RestController
@RequestMapping("/api/observatory/skills")
public class ObservatorySkillController {
    private final SkillCatalogService service;
    public ObservatorySkillController(SkillCatalogService service) { this.service = service; }
    @GetMapping public List<SkillVersion> list() { return service.list(); }
    @GetMapping("/{id}/history") public List<SkillVersion> history(@PathVariable UUID id) { return service.history(id); }
    @PostMapping public ResponseEntity<SkillVersion> create(@RequestBody SkillCatalogService.Edit edit) {
        var skill = service.create(edit);
        return ResponseEntity.created(URI.create("/api/observatory/skills/" + skill.id() + "/history")).body(skill);
    }
    @PutMapping("/{id}") public SkillVersion revise(@PathVariable UUID id, @RequestBody SkillCatalogService.Edit edit) { return service.revise(id, edit); }
}
