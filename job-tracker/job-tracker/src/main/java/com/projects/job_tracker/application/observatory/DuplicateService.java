package com.projects.job_tracker.application.observatory;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.DuplicateModels.*;

@Service
@Transactional
public class DuplicateService {
    private final DuplicateRepository repository;
    private final MarketPostingRepository postings;
    public DuplicateService(DuplicateRepository repository, MarketPostingRepository postings) { this.repository=repository; this.postings=postings; }
    public void ensureAvailable(String source, String url, String externalId, String conflictReason) {
        if (conflictReason != null) CaptureModels.required(conflictReason,"Motivo para registro independiente",4000);
        repository.lockRegistry();
        var matches=matching(source,url,externalId);
        if (!matches.isEmpty() && (conflictReason == null || conflictReason.isBlank())) throw new DuplicateIdentityException(matches);
    }
    public List<UUID> resolveExisting(String source,String url,String externalId) {
        repository.lockRegistry();
        return matching(source,url,externalId);
    }
    private List<UUID> matching(String source,String url,String externalId) {
        String normalizedUrl=SourceIdentityKey.url(url),external=SourceIdentityKey.externalId(externalId);
        return repository.claimsBySource(SourceIdentityKey.source(source)).stream().filter(c ->
            (normalizedUrl!=null && normalizedUrl.equals(SourceIdentityKey.url(c.url()))) || (external!=null && external.equals(c.externalId())))
            .map(SourceClaim::postingId).toList();
    }
    public void attach(UUID id, String source, String url, String externalId, String conflictReason) {
        repository.attach(new SourceClaim(id,SourceIdentityKey.source(source),url,SourceIdentityKey.externalId(externalId),CaptureModels.optional(conflictReason)));
    }
    public record Decide(UUID leftId, UUID rightId, Integer expectedRevision, Integer leftRevision,
            Integer rightRevision, Decision decision, String reason) {}
    private record Pair(UUID left, UUID right) {
        static Pair of(UUID a, UUID b) { return a.toString().compareTo(b.toString()) <= 0 ? new Pair(a,b) : new Pair(b,a); }
    }
    public Review decide(Decide command) {
        if (command == null || command.leftId() == null || command.rightId() == null || command.leftId().equals(command.rightId()) || command.decision() == null)
            throw new IllegalArgumentException("Selecciona dos anuncios diferentes y una decisión");
        if (command.expectedRevision() == null || command.expectedRevision() < 0 || command.expectedRevision() == Integer.MAX_VALUE
                || command.leftRevision() == null || command.rightRevision() == null)
            throw new IllegalArgumentException("Indica la revisión de la decisión y de ambos anuncios");
        String reason = CaptureModels.required(command.reason(),"Motivo de revisión",4000);
        Pair pair = Pair.of(command.leftId(),command.rightId());
        int leftRevision = pair.left().equals(command.leftId()) ? command.leftRevision() : command.rightRevision();
        int rightRevision = pair.right().equals(command.rightId()) ? command.rightRevision() : command.leftRevision();
        String hash = EvidenceHash.sha256("DUPLICATE_REVIEW_V1|"+pair+"|"+command.expectedRevision()+"|"+leftRevision+"|"+rightRevision+"|"+command.decision()+"|"+reason);
        repository.lockRegistry();
        List<Review> current = repository.currentReviews(Instant.now());
        Review previous = current.stream().filter(r -> r.leftId().equals(pair.left()) && r.rightId().equals(pair.right())).findFirst().orElse(null);
        if (previous != null && previous.revision() == command.expectedRevision()+1 && previous.requestHash().equals(hash)) return previous;
        if ((previous == null ? 0 : previous.revision()) != command.expectedRevision()) throw new IllegalArgumentException("La revisión cambió; recarga antes de decidir");
        if (previous == null && command.decision() == Decision.UNREVIEWED) throw new IllegalArgumentException("No hay una decisión que deshacer");
        postings.lock(pair.left()); postings.lock(pair.right());
        var snapshots = repository.snapshots();
        Snapshot left = snapshot(snapshots,pair.left()), right = snapshot(snapshots,pair.right());
        if (left.revision() != leftRevision || right.revision() != rightRevision)
            throw new IllegalArgumentException("Los anuncios cambiaron; vuelve a revisar sus datos");
        var others = current.stream().filter(r -> !r.leftId().equals(pair.left()) || !r.rightId().equals(pair.right())).toList();
        var partition = new Partition(snapshots,others);
        if (command.decision() == Decision.MERGED) {
            if (partition.same(pair.left(),pair.right())) throw new IllegalArgumentException("Ya pertenecen al mismo grupo; no se crean uniones redundantes");
            partition.join(pair.left(),pair.right());
            if (others.stream().anyMatch(r -> r.decision() == Decision.DISTINCT && partition.same(r.leftId(),r.rightId())))
                throw new IllegalArgumentException("La unión contradice una decisión de anuncios distintos; revisa esa decisión primero");
        } else if (command.decision() == Decision.DISTINCT && partition.same(pair.left(),pair.right())) {
            throw new IllegalArgumentException("Deshaz primero la unión que los mantiene en el mismo grupo");
        }
        Review review = new Review(previous == null ? UUID.randomUUID() : previous.id(),pair.left(),pair.right(),command.expectedRevision()+1,
            command.decision(),reason,leftRevision,rightRevision,Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS),hash);
        repository.save(review,previous == null);
        return review;
    }
    @Transactional(readOnly=true)
    public List<ReviewView> reviews() {
        var snapshots = repository.snapshots();
        return repository.currentReviews(Instant.now()).stream().map(r -> new ReviewView(r,
            snapshot(snapshots,r.leftId()).revision()!=r.leftRevision() || snapshot(snapshots,r.rightId()).revision()!=r.rightRevision())).toList();
    }
    @Transactional(readOnly=true)
    public List<Review> history(UUID id) {
        var history = repository.history(id);
        if (history.isEmpty()) throw new ResourceNotFoundException("Revisión de duplicados no encontrada");
        return history;
    }
    @Transactional(readOnly=true)
    public Identity identity(UUID id, Instant at) {
        Instant cutoff = at == null ? Instant.now() : at;
        var snapshots = repository.snapshots().stream().filter(s -> !s.recordedAt().isAfter(cutoff)).toList();
        snapshot(snapshots,id);
        var partition = new Partition(snapshots,repository.currentReviews(cutoff));
        var members = snapshots.stream().map(Snapshot::id).filter(member -> partition.same(id,member)).sorted(Comparator.comparing(UUID::toString)).toList();
        return new Identity(repository.claim(id).orElseThrow(),partition.root(id),members);
    }
    @Transactional(readOnly=true)
    public List<Group> groups() {
        var snapshots = repository.snapshots(); var partition = new Partition(snapshots,repository.currentReviews(Instant.now()));
        Map<UUID,List<Snapshot>> groups = new TreeMap<>(Comparator.comparing(UUID::toString));
        for (Snapshot snapshot : snapshots) groups.computeIfAbsent(partition.root(snapshot.id()),k -> new ArrayList<>()).add(snapshot);
        return groups.entrySet().stream().filter(e -> e.getValue().size()>1).map(e -> new Group(e.getKey(),List.copyOf(e.getValue()))).toList();
    }
    private record SignalKey(String signal,List<String> parts) {}
    @Transactional(readOnly=true)
    public List<Candidate> candidates(int limit, int offset) {
        if (limit<1 || limit>100 || offset<0) throw new IllegalArgumentException("Paginación no válida (1–100)");
        var snapshots = repository.snapshots(); var reviews = repository.currentReviews(Instant.now());
        var partition = new Partition(snapshots,reviews);
        Map<Pair,Review> decisions = new HashMap<>();
        for (Review review : reviews) decisions.put(new Pair(review.leftId(),review.rightId()),review);
        Map<SignalKey,List<Snapshot>> buckets = new LinkedHashMap<>();
        Map<UUID,Snapshot> byId = new HashMap<>();
        for (Snapshot snapshot : snapshots) {
            byId.put(snapshot.id(),snapshot);
            String source = SourceIdentityKey.source(snapshot.source()), url=SourceIdentityKey.url(snapshot.url());
            if (url!=null) add(buckets,new SignalKey("SOURCE_URL",List.of(source,url)),snapshot);
            if (snapshot.externalId()!=null) add(buckets,new SignalKey("SOURCE_EXTERNAL_ID",List.of(source,snapshot.externalId())),snapshot);
            if (snapshot.originalText()!=null && snapshot.originalText().strip().length()>=80)
                add(buckets,new SignalKey("EXACT_CONTENT",List.of(snapshot.contentHash()==null ? EvidenceHash.sha256(snapshot.originalText()) : snapshot.contentHash())),snapshot);
            if (snapshot.company()!=null && !snapshot.company().isBlank())
                add(buckets,new SignalKey("TITLE_COMPANY",List.of(fold(snapshot.title()),fold(snapshot.company()))),snapshot);
        }
        Map<Pair,SortedSet<String>> signals = new HashMap<>();
        for (var entry : buckets.entrySet()) {
            var bucket = entry.getValue();
            for (int i=0;i<bucket.size();i++) for (int j=i+1;j<bucket.size();j++) {
                Pair pair = Pair.of(bucket.get(i).id(),bucket.get(j).id());
                Review decision = decisions.get(pair);
                if (partition.same(pair.left(),pair.right()) || (decision!=null && decision.decision()==Decision.DISTINCT)) continue;
                signals.computeIfAbsent(pair,k -> new TreeSet<>()).add(entry.getKey().signal());
            }
        }
        return signals.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().left().toString()+e.getKey().right()))
            .skip(offset).limit(limit).map(e -> new Candidate(byId.get(e.getKey().left()),byId.get(e.getKey().right()),List.copyOf(e.getValue()),
                decisions.containsKey(e.getKey()) ? decisions.get(e.getKey()).revision() : 0)).toList();
    }
    private static void add(Map<SignalKey,List<Snapshot>> buckets,SignalKey key,Snapshot snapshot) { buckets.computeIfAbsent(key,k -> new ArrayList<>()).add(snapshot); }
    private static String fold(String value) { return value.strip().toLowerCase(Locale.ROOT).replaceAll("\\s+"," "); }
    private static Snapshot snapshot(List<Snapshot> snapshots,UUID id) {
        return snapshots.stream().filter(s -> s.id().equals(id)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado para esa fecha"));
    }
    /** Active merge links form a forest, so undoing one edge really separates its components. */
    private static final class Partition {
        private final Map<UUID,UUID> parents = new HashMap<>();
        Partition(List<Snapshot> snapshots,List<Review> reviews) {
            for (Snapshot snapshot : snapshots) parents.put(snapshot.id(),snapshot.id());
            for (Review review : reviews) if (review.decision()==Decision.MERGED && parents.containsKey(review.leftId()) && parents.containsKey(review.rightId())) join(review.leftId(),review.rightId());
        }
        UUID root(UUID id) {
            UUID cursor = id;
            while (!parents.get(cursor).equals(cursor)) cursor=parents.get(cursor);
            UUID result=cursor; cursor=id;
            while (!parents.get(cursor).equals(cursor)) { UUID next=parents.get(cursor); parents.put(cursor,result); cursor=next; }
            return result;
        }
        boolean same(UUID a,UUID b) { return root(a).equals(root(b)); }
        void join(UUID a,UUID b) {
            UUID left=root(a),right=root(b);
            if (left.toString().compareTo(right.toString())<=0) parents.put(right,left); else parents.put(left,right);
        }
    }
}
