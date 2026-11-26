package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.*;

/** Explicit manual sampling contracts, independent of application tracking. */
public final class CaptureModels {
    private CaptureModels() {}
    public enum RunState { RUNNING, COMPLETE, PARTIAL, FAILED }
    public enum SamplingMode { EXPLORATORY, SYSTEMATIC_MANUAL }
    public enum Availability { AVAILABLE, UNAVAILABLE, UNKNOWN }
    public record SegmentSpec(String name, String description, String roleFamily, String countryCode,
            PostingContext.Seniority seniority, PostingContext.WorkMode workMode) {
        public SegmentSpec {
            name = required(name, "Nombre del segmento", 200);
            roleFamily = required(roleFamily, "Familia de puesto", 200);
            description = optional(description);
            countryCode = optional(countryCode);
            if (countryCode != null) {
                countryCode = countryCode.toUpperCase(Locale.ROOT);
                if (!Set.of(Locale.getISOCountries()).contains(countryCode)) throw new IllegalArgumentException("País del segmento no válido");
            }
        }
    }
    public record SegmentVersion(UUID id, int revision, SegmentSpec spec, String reason, Instant recordedAt) {}
    public record ProtocolSpec(UUID segmentId, Integer segmentRevision, String name, String source, String query,
            String filters, Integer intervalMinutes, Integer resultLimit, String ordering, String pagination,
            SamplingMode samplingMode, CollectionPlan collectionPlan) {
        public ProtocolSpec(UUID segmentId, Integer segmentRevision, String name, String source, String query,
                String filters, Integer intervalMinutes, Integer resultLimit, String ordering, String pagination, SamplingMode samplingMode) {
            this(segmentId,segmentRevision,name,source,query,filters,intervalMinutes,resultLimit,ordering,pagination,samplingMode,null);
        }
        public ProtocolSpec {
            if (segmentId == null || segmentRevision == null || segmentRevision < 1) throw new IllegalArgumentException("Indica la versión del segmento");
            name = required(name, "Nombre del protocolo", 200);
            source = required(source, "Fuente", 200);
            query = required(query, "Consulta", 1000);
            filters = required(filters, "Filtros (escribir Sin filtros si corresponde)", 4000);
            ordering = required(ordering, "Orden de resultados", 500);
            pagination = required(pagination, "Regla de paginación", 500);
            if (intervalMinutes == null || intervalMinutes < 1 || intervalMinutes > 525600)
                throw new IllegalArgumentException("Frecuencia entre 1 y 525600 minutos");
            if (resultLimit == null || resultLimit < 1 || resultLimit > 100000) throw new IllegalArgumentException("Límite entre 1 y 10000 resultados");
            if (collectionPlan != null) {
                if (!collectionPlan.platform().source().equalsIgnoreCase(source)) throw new IllegalArgumentException("La fuente debe coincidir con el portal configurado");
                if (samplingMode != SamplingMode.EXPLORATORY) throw new IllegalArgumentException("La captura bajo demanda debe ser exploratoria");
                if (resultLimit > 1000) throw new IllegalArgumentException("La captura automática admite hasta 1000 elementos");
                if (!"Orden del portal".equals(ordering) || !collectionPlan.paginationDescription().equals(pagination) || !collectionPlan.filterDescription().equals(filters))
                    throw new IllegalArgumentException("Los filtros, orden y paginación deben describir el plan ejecutable");
            }
            if (samplingMode == null) throw new IllegalArgumentException("Indica el tipo de muestra");
        }
    }
    public record ProtocolVersion(UUID id, int revision, ProtocolSpec spec, String reason, Instant recordedAt) {}
    public record CaptureRun(UUID id, UUID protocolId, int protocolRevision, UUID requestKey, Instant startedAt,
            Instant finishedAt, RunState state, Integer pagesVisited, boolean truncated, String notes, long observationCount) {}
    public record Observation(UUID id, UUID postingId, UUID runId, String itemKey, String source, String url,
            String originalText, String contentHash, Instant observedAt, Instant recordedAt,
            Availability availability, Integer dataRevision) {}
    public static String required(String value, String label, int max) {
        if (value == null || value.isBlank() || value.strip().length() > max) throw new IllegalArgumentException(label + " es obligatorio (máximo " + max + ")");
        return value.strip();
    }
    public static String optional(String value) { return value == null || value.isBlank() ? null : value.strip(); }
}
