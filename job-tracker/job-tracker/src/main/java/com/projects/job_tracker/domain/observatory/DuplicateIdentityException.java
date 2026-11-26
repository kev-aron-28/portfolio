package com.projects.job_tracker.domain.observatory;

import java.util.*;

public class DuplicateIdentityException extends RuntimeException {
    private final List<UUID> matches;
    public DuplicateIdentityException(List<UUID> matches) {
        super("Ya existe un anuncio con esa fuente e identificador o URL. Abre el registro existente para conservar una nueva observación.");
        this.matches = List.copyOf(matches);
    }
    public List<UUID> getMatches() { return matches; }
}
