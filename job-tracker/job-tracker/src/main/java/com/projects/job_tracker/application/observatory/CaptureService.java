package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.CaptureModels.*;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import static com.projects.job_tracker.domain.observatory.CaptureModels.*;

@Service
@Transactional
public class CaptureService {
    private final CaptureRepository repository;
    private final MarketPostingRepository postings;
    private final SkillCatalogService catalog;
    private final CollectionLedger collectionLedger;
    public CaptureService(CaptureRepository repository, MarketPostingRepository postings, SkillCatalogService catalog, CollectionLedger collectionLedger) {
        this.repository = repository; this.postings = postings; this.catalog = catalog; this.collectionLedger=collectionLedger;
    }
    public record SegmentEdit(Integer expectedRevision, SegmentSpec spec, String reason) {}
    public record ProtocolEdit(Integer expectedRevision, ProtocolSpec spec, String reason) {}
    public record Start(Integer protocolRevision, UUID requestKey) {}
    public record Finish(RunState state, Integer pagesVisited, Boolean truncated, String notes) {}
    public record Observe(String itemKey, UUID postingId, Integer expectedRevision, Instant observedAt,
            Availability availability, String originalText, PostingData data) {}
    public SegmentVersion saveSegment(UUID id, SegmentEdit edit) {
        if (edit == null || edit.spec() == null) throw new IllegalArgumentException("Falta la definición del segmento");
        int revision = next(id, edit.expectedRevision());
        var version = new SegmentVersion(id == null ? UUID.randomUUID() : id, revision, edit.spec(), required(edit.reason(), "Motivo", 4000), Instant.now());
        repository.saveSegment(version, id == null ? null : edit.expectedRevision());
        return version;
    }
    public ProtocolVersion saveProtocol(UUID id, ProtocolEdit edit) {
        if (edit == null || edit.spec() == null) throw new IllegalArgumentException("Falta la definición del protocolo");
        var spec = edit.spec();
        if (segmentHistory(spec.segmentId()).stream().noneMatch(v -> v.revision() == spec.segmentRevision()))
            throw new IllegalArgumentException("La versión del segmento no existe");
        var version = new ProtocolVersion(id == null ? UUID.randomUUID() : id, next(id, edit.expectedRevision()), spec, required(edit.reason(), "Motivo", 4000), Instant.now());
        repository.saveProtocol(version, id == null ? null : edit.expectedRevision());
        return version;
    }
    private int next(UUID id, Integer expected) {
        if (id == null) return 1;
        if (expected == null || expected < 1 || expected == Integer.MAX_VALUE) throw new IllegalArgumentException("Indica una revisión actual válida");
        return expected + 1;
    }
    @Transactional(readOnly = true) public List<SegmentVersion> segments() { return repository.segments(); }
    @Transactional(readOnly = true) public List<ProtocolVersion> protocols() { return repository.protocols(); }
    @Transactional(readOnly = true) public List<SegmentVersion> segmentHistory(UUID id) {
        var result = repository.segmentHistory(id);
        if (result.isEmpty()) throw new ResourceNotFoundException("Segmento no encontrado");
        return result;
    }
    @Transactional(readOnly = true) public List<ProtocolVersion> protocolHistory(UUID id) {
        var result = repository.protocolHistory(id);
        if (result.isEmpty()) throw new ResourceNotFoundException("Protocolo no encontrado");
        return result;
    }
    public CaptureRun start(UUID protocolId, Start command) {
        if (command == null || command.requestKey() == null || command.protocolRevision() == null)
            throw new IllegalArgumentException("Indica versión de protocolo y clave de solicitud");
        repository.lockProtocol(protocolId);
        var prior = repository.findRunByKey(protocolId, command.requestKey());
        if (prior.isPresent()) {
            if (prior.get().protocolRevision() != command.protocolRevision()) throw new IllegalArgumentException("La clave se usó con otra versión del protocolo");
            return prior.get();
        }
        protocol(protocolId, command.protocolRevision());
        var run = new CaptureRun(UUID.randomUUID(), protocolId, command.protocolRevision(), command.requestKey(), Instant.now(), null, RunState.RUNNING, null, false, null, 0);
        repository.insertRun(run);
        return run;
    }
    @Transactional(readOnly = true) public CaptureRun run(UUID id) {
        return repository.findRun(id).orElseThrow(() -> new ResourceNotFoundException("Ejecución no encontrada"));
    }
    @Transactional(readOnly = true) public List<CaptureRun> runs(int limit, int offset) {
        if (limit < 1 || limit > 100 || offset < 0) throw new IllegalArgumentException("Paginación no válida (1–100)");
        return repository.runs(limit, offset);
    }
    @Transactional(readOnly = true) public List<Observation> observations(UUID runId) { run(runId); return repository.runObservations(runId); }
    private ProtocolVersion protocol(UUID id, int revision) {
        return protocolHistory(id).stream().filter(v -> v.revision() == revision).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("La versión del protocolo no existe"));
    }
    public Observation observe(UUID runId, Observe command) {
        if (command==null) throw new IllegalArgumentException("Faltan datos de observación");
        repository.lockRun(runId);
        if (collectionLedger.attempt(runId).isPresent()) throw new IllegalArgumentException("Esta ejecución pertenece al recolector; utiliza otra para observaciones manuales");
        return observeWithFingerprint(runId,command,EvidenceHash.sha256(command.toString()),"MANUAL_OBSERVATION_V1");
    }
    public Observation observeWithFingerprint(UUID runId,Observe command,String fingerprint,String method) {
        return observeWithFingerprint(runId,command,fingerprint,method,null,false);
    }
    public Observation observeWithFingerprint(UUID runId, Observe command, String fingerprint, String method,String observedUrl,boolean explicitUrl) {
        if (command == null || command.postingId() == null || command.availability() == null || command.observedAt() == null)
            throw new IllegalArgumentException("Faltan datos de la observación");
        String itemKey = required(command.itemKey(), "Clave del elemento", 200);
        var run = repository.lockRun(runId);
        // Replays must also work after finishing a run, without writing or revalidating stale revisions.
        var replay = repository.findItem(runId, itemKey, fingerprint);
        if (replay.isPresent()) return replay.get();
        if (run.state() != RunState.RUNNING) throw new IllegalArgumentException("La ejecución ya terminó");
        if (command.observedAt().isAfter(Instant.now()) || command.observedAt().isBefore(run.startedAt()))
            throw new IllegalArgumentException("La observación debe ocurrir durante la ejecución y no puede ser futura");
        postings.lock(command.postingId());
        MarketPosting posting = postings.find(command.postingId()).orElseThrow();
        if (command.expectedRevision() == null || posting.current().number() != command.expectedRevision())
            throw new IllegalArgumentException("El anuncio cambió; recarga antes de observar");
        if (posting.observations().stream().anyMatch(o -> o.observedAt().isAfter(command.observedAt())))
            throw new IllegalArgumentException("Registra observaciones en orden temporal; la fecha es anterior a una ya guardada");
        var protocol = protocol(run.protocolId(), run.protocolRevision());
        if (!protocol.spec().source().equalsIgnoreCase(posting.source()))
            throw new IllegalArgumentException("La fuente del protocolo no coincide con la del anuncio");
        String original = command.originalText();
        if (command.availability() == Availability.AVAILABLE) {
            if (original == null || original.isBlank()) throw new IllegalArgumentException("Conserva el texto observado");
            long availableCount = repository.runObservations(runId).stream().filter(o -> o.availability() == Availability.AVAILABLE).count();
            if (availableCount >= protocol.spec().resultLimit()) throw new IllegalArgumentException("Se alcanzó el límite del protocolo; registra una ejecución parcial");
        } else if (original != null || command.data() != null) {
            throw new IllegalArgumentException("Una observación no disponible/desconocida no lleva texto ni transcripción");
        }
        boolean newRevision = false;
        if (command.availability() == Availability.AVAILABLE) {
            String previousOriginal = posting.evidenceForCurrentRevision().originalText();
            if (!original.equals(previousOriginal) && command.data() == null)
                throw new IllegalArgumentException("El contenido cambió: incluye la transcripción actualizada");
            if (command.data() != null) {
                MarketPostingService.validateContext(command.data(), original, command.observedAt());
                catalog.validate(command.data(), original);
                newRevision = !original.equals(previousOriginal) || !command.data().equals(posting.current().data());
            } else {
                MarketPostingService.validateContext(posting.current().data(), original, command.observedAt());
            }
        }
        if (explicitUrl) SourceIdentityKey.url(observedUrl);
        Instant now = Instant.now();
        var observation = new Observation(UUID.randomUUID(), posting.id(), runId, itemKey, posting.source(), explicitUrl ? observedUrl : posting.url(),
            original, original == null ? null : EvidenceHash.sha256(original), command.observedAt(), now, command.availability(),
            command.availability() == Availability.AVAILABLE && !newRevision ? posting.current().number() : null);
        repository.insertObservation(observation, fingerprint);
        if (newRevision) {
            int number = posting.current().number() + 1;
            postings.append(posting.id(), posting.current().number(), new MarketPosting.Revision(number, now,
                "Nueva observación en captura " + runId, method, command.data(), observation.id()));
            repository.linkRevision(observation.id(), number);
            return new Observation(observation.id(), observation.postingId(), observation.runId(), observation.itemKey(), observation.source(), observation.url(),
                observation.originalText(), observation.contentHash(), observation.observedAt(), observation.recordedAt(), observation.availability(), number);
        }
        return observation;
    }
    public CaptureRun finish(UUID id, Finish command) { return finishInternal(id,command,false); }
    public CaptureRun finishCollected(UUID id,Finish command) { return finishInternal(id,command,true); }
    private CaptureRun finishInternal(UUID id,Finish command,boolean fromCollector) {
        if (command == null || command.state() == null || command.state() == RunState.RUNNING || command.pagesVisited() == null
                || command.pagesVisited() < 0 || command.truncated() == null) throw new IllegalArgumentException("Indica estado final, páginas visitadas y truncamiento");
        String notes = optional(command.notes());
        if (command.state() != RunState.COMPLETE && notes == null) throw new IllegalArgumentException("Describe el motivo de captura parcial o fallida");
        if (command.truncated() && command.state() != RunState.PARTIAL) throw new IllegalArgumentException("Una captura truncada debe finalizar como parcial");
        var run = repository.lockRun(id);
        if (run.state() != RunState.RUNNING) {
            if (run.state() == command.state() && Objects.equals(run.pagesVisited(), command.pagesVisited()) && run.truncated() == command.truncated() && Objects.equals(run.notes(), notes)) return run;
            throw new IllegalArgumentException("La ejecución ya terminó con otros datos");
        }
        if (!fromCollector && collectionLedger.attempt(id).filter(a -> a.status().equals("RUNNING")).isPresent())
            throw new IllegalArgumentException("El recolector sigue activo; consulta su estado antes de finalizar manualmente");
        var observations = repository.runObservations(id);
        if (command.state() == RunState.COMPLETE && observations.stream().anyMatch(o -> o.availability() == Availability.UNKNOWN))
            throw new IllegalArgumentException("Hay observaciones sin determinar; finaliza como parcial o fallida con motivo");
        long availableCount = observations.stream().filter(o -> o.availability() == Availability.AVAILABLE).count();
        if (availableCount > 0 && command.state() == RunState.FAILED) throw new IllegalArgumentException("Con resultados disponibles, utiliza estado parcial");
        if (availableCount >= protocol(run.protocolId(), run.protocolRevision()).spec().resultLimit() && !command.truncated())
            throw new IllegalArgumentException("Se alcanzó el límite; marca truncamiento y estado parcial");
        var finished = new CaptureRun(run.id(), run.protocolId(), run.protocolRevision(), run.requestKey(), run.startedAt(), Instant.now(),
            command.state(), command.pagesVisited(), command.truncated(), notes, run.observationCount());
        repository.finish(finished);
        return finished;
    }
}
