package com.projects.job_tracker.application.observatory;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.observatory.*;

@Service
@Transactional
public class MarketPostingService {
    private final MarketPostingRepository repository;
    private final SkillCatalogService catalog;
    private final DuplicateService identities;
    public MarketPostingService(MarketPostingRepository repository, SkillCatalogService catalog, DuplicateService identities) {
        this.repository = repository; this.catalog = catalog; this.identities = identities;
    }
    public record Registration(String source, String url, String originalText, Instant observedAt, PostingData data, String externalId, String identityConflictReason) {}
    public record Correction(int expectedRevision, String reason, PostingData data) {}
    public MarketPosting register(Registration request) {
        if (request == null || request.data() == null) throw new IllegalArgumentException("Faltan datos del anuncio");
        if (request.source() == null || request.source().isBlank() || request.source().length() > 200)
            throw new IllegalArgumentException("Indica una fuente de hasta 200 caracteres");
        if (request.originalText() == null || request.originalText().isBlank())
            throw new IllegalArgumentException("Conserva el texto original del anuncio");
        Instant now = Instant.now();
        if (request.observedAt() == null || request.observedAt().isAfter(now))
            throw new IllegalArgumentException("Indica una fecha de observación que no sea futura");
        String url = request.url() == null || request.url().isBlank() ? null : request.url().strip();
        if (url != null) {
            URI uri = URI.create(url);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null)
                throw new IllegalArgumentException("La URL debe ser HTTP o HTTPS y tener un dominio");
        }
        validateContext(request.data(), request.originalText(), request.observedAt());
        catalog.validate(request.data(), request.originalText());
        identities.ensureAvailable(request.source(), url, request.externalId(),request.identityConflictReason());
        MarketPosting posting = new MarketPosting(UUID.randomUUID(), request.source().strip(), url,
            request.originalText(), request.observedAt(), now,
            List.of(new MarketPosting.Revision(1, now, "Registro manual inicial", "MANUAL_V1", request.data())));
        repository.insert(posting);
        identities.attach(posting.id(),posting.source(),posting.url(),request.externalId(),request.identityConflictReason());
        return get(posting.id());
    }
    @Transactional(readOnly = true)
    public MarketPosting get(UUID id) {
        return repository.find(id).orElseThrow(() -> new ResourceNotFoundException("Anuncio del observatorio no encontrado"));
    }
    @Transactional(readOnly = true)
    public List<MarketPosting> list(int limit, int offset) {
        if (limit < 1 || limit > 100 || offset < 0) throw new IllegalArgumentException("Paginación no válida (límite 1–100)");
        return repository.list(limit, offset);
    }
    public MarketPosting correct(UUID id, Correction correction) {
        if (correction == null || correction.data() == null || correction.reason() == null || correction.reason().isBlank())
            throw new IllegalArgumentException("Indica datos completos y motivo de corrección");
        var existing = get(id);
        var evidence = existing.evidenceForCurrentRevision();
        validateContext(correction.data(), evidence.originalText(), evidence.observedAt());
        catalog.validate(correction.data(), evidence.originalText());
        repository.append(id, correction.expectedRevision(), new MarketPosting.Revision(
            correction.expectedRevision() + 1, Instant.now(), correction.reason().strip(), "HUMAN_CORRECTION_V1", correction.data(), evidence.id()));
        return get(id);
    }
    public static void validateContext(PostingData data, String original, Instant observedAt) {
        var context = data.context();
        if (context.evidence() != null && !original.contains(context.evidence()))
            throw new IllegalArgumentException("La evidencia del contexto debe ser una cita exacta del original");
        if (context.publishedAt() != null && context.publishedAt().isAfter(observedAt))
            throw new IllegalArgumentException("La publicación no puede ser posterior a la observación");
    }

}
