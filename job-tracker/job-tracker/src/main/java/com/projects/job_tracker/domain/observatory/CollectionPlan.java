package com.projects.job_tracker.domain.observatory;

import com.projects.job_tracker.domain.model.JobPlatform;

/** Only filters that the selected source can translate exactly are accepted. Bounded page collection. */
public record CollectionPlan(JobPlatform platform, String location, PostingContext.WorkMode workMode, Integer postedWithinDays, Integer maxPages) {
    public CollectionPlan(JobPlatform platform,String location,PostingContext.WorkMode workMode,Integer postedWithinDays) {
        this(platform,location,workMode,postedWithinDays,1);
    }
    public CollectionPlan {
        maxPages = maxPages == null ? 1 : maxPages;
        if (maxPages < 1 || maxPages > 20) throw new IllegalArgumentException("Límite entre 1 y 20 páginas");
        if (platform == null) throw new IllegalArgumentException("Selecciona un portal");
        location = CaptureModels.optional(location);
        if (postedWithinDays != null && (postedWithinDays < 1 || postedWithinDays > 365)) throw new IllegalArgumentException("Antigüedad entre 1 y 365 días");
        if (platform == JobPlatform.LINKEDIN && postedWithinDays == null) throw new IllegalArgumentException("LinkedIn requiere antigüedad explícita; no se aplicará una semana por defecto");
        if (platform == JobPlatform.INDEED && workMode != null && workMode != PostingContext.WorkMode.REMOTE)
            throw new IllegalArgumentException("Indeed sólo traduce el filtro remoto en esta versión");
        if (platform == JobPlatform.COMPUTRABAJO) {
            if (workMode == PostingContext.WorkMode.ONSITE) throw new IllegalArgumentException("El filtro presencial de Computrabajo no está soportado");
            if (workMode != null && location != null) throw new IllegalArgumentException("Computrabajo no combina ubicación y modalidad en este conector");
            if (postedWithinDays != null && !java.util.Set.of(1,3,7,15,30).contains(postedWithinDays))
                throw new IllegalArgumentException("Computrabajo admite exactamente 1, 3, 7, 15 o 30 días");
        }
    }
    public String paginationDescription() { return maxPages == 1 ? "Primera página" : "Hasta " + maxPages + " páginas"; }
    public String filterDescription() {
        return "Ubicación: " + (location == null ? "sin filtro" : location) + "; modalidad: " +
            (workMode == null ? "sin filtro" : switch(workMode) { case REMOTE -> "remota"; case HYBRID -> "híbrida"; case ONSITE -> "presencial"; }) +
            "; antigüedad: " + (postedWithinDays == null ? "sin filtro" : postedWithinDays + " días");
    }
}
