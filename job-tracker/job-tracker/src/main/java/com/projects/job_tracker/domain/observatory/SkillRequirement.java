package com.projects.job_tracker.domain.observatory;

import java.util.UUID;

/** References an immutable catalog version. Evidence is an exact excerpt of the original. */
public record SkillRequirement(UUID skillId, int skillRevision, Obligation obligation, String evidence) {
    public enum Obligation { REQUIRED, DESIRED, UNKNOWN }
    public SkillRequirement {
        if (skillId == null || skillRevision < 1 || obligation == null || evidence == null || evidence.isBlank())
            throw new IllegalArgumentException("Cada requisito necesita habilidad, versión, obligatoriedad y evidencia");
    }
}
