package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;

@Service
@Transactional
public class SkillCatalogService {
    private final SkillRepository repository;
    public SkillCatalogService(SkillRepository repository) { this.repository = repository; }
    public record Edit(Integer expectedRevision, String name, List<String> aliases, String reason) {}
    @Transactional(readOnly = true)
    public List<SkillVersion> list() { return repository.list(); }
    @Transactional(readOnly = true)
    public List<SkillVersion> history(UUID id) {
        var versions = repository.history(id);
        if (versions.isEmpty()) throw new ResourceNotFoundException("Habilidad no encontrada");
        return versions;
    }
    public SkillVersion create(Edit edit) {
        SkillVersion skill = new SkillVersion(UUID.randomUUID(), 1, edit.name(), edit.aliases(), edit.reason(), Instant.now());
        repository.create(skill);
        return skill;
    }
    public SkillVersion revise(UUID id, Edit edit) {
        if (edit.expectedRevision() == null || edit.expectedRevision() < 1) throw new IllegalArgumentException("Indica la versión actual de la habilidad");
        SkillVersion skill = new SkillVersion(id, edit.expectedRevision() + 1, edit.name(), edit.aliases(), edit.reason(), Instant.now());
        repository.append(skill, edit.expectedRevision());
        return skill;
    }
    public void validate(PostingData data, String original) {
        for (SkillRequirement requirement : data.skillRequirements()) {
            if (!original.contains(requirement.evidence()))
                throw new IllegalArgumentException("La evidencia debe ser un fragmento exacto del texto original");
            if (repository.history(requirement.skillId()).stream().noneMatch(v -> v.revision() == requirement.skillRevision()))
                throw new IllegalArgumentException("La versión de habilidad indicada no existe");
        }
    }
}
