package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;

public record SkillVersion(UUID id, int revision, String name, List<String> aliases,
        String reason, Instant recordedAt) {
    public SkillVersion {
        if (name == null || name.isBlank() || name.strip().length() > 200)
            throw new IllegalArgumentException("La habilidad requiere un nombre de hasta 200 caracteres");
        name = name.strip();
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Indica el motivo del cambio de catálogo");
        reason = reason.strip();
        aliases = aliases == null ? List.of() : aliases;
        if (aliases.size() > 30 || aliases.stream().anyMatch(a -> a == null || a.isBlank() || a.strip().length() > 200))
            throw new IllegalArgumentException("Alias no válidos (hasta 30, de 200 caracteres)");
        aliases = aliases.stream().map(String::strip).toList();
        var names = new HashSet<String>();
        names.add(name.toLowerCase(Locale.ROOT));
        for (String alias : aliases)
            if (!names.add(alias.toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("Nombre y alias deben ser diferentes");
    }
}
