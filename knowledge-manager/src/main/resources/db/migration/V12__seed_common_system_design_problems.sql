-- Seed common system design interview problems with typical design docs.

BEGIN;

-- Design a URL Shortener
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a URL Shortener',
    'STORAGE',
    'MEDIUM',
    'Design a URL shortening service like TinyURL or bit.ly.

Users submit a long URL and receive a short unique link. Visiting the short link redirects to the original URL.
Support custom aliases (optional), link expiration, and basic click analytics.',
    'Classic: TinyURL / bit.ly',
    45,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a URL Shortener'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'A URL shortener maps long URLs to short codes and serves fast redirects with high availability.',
       '- Create a short URL from a long URL
- Redirect short URL → long URL with HTTP 301/302
- Optional custom alias
- Optional expiration
- Track click counts (basic analytics)',
       '- Redirect latency: low (p99 < 50–100ms)
- High availability for reads/redirects
- Write throughput for link creation
- Short codes should be unique and hard to guess',
       '- Mostly read-heavy (redirects >> creates)
- Short codes ~6–8 characters are enough for expected volume
- Analytics can be eventually consistent',
       'Client → API/LB → Shortener service → DB (mapping) + Cache (hot redirects)
Optional: analytics pipeline via queue → analytics store',
       '- API service (create + resolve)
- Key generation service (hash / counter + base62)
- Primary datastore for mappings
- Cache (Redis) for popular short codes
- Analytics workers',
       'Table `urls`:
- short_code (PK)
- long_url
- user_id (nullable)
- created_at, expires_at
- click_count

Index long_url (optional uniqueness if you dedupe).',
       'POST /api/v1/urls { longUrl, customAlias?, ttl? } → { shortUrl }
GET /{code} → 302 Location: longUrl
GET /api/v1/urls/{code}/stats → { clicks, createdAt }',
       '- Cache redirects aggressively
- Shard DB by short_code if needed
- Separate write path (create) from hot read path (redirect)',
       '- Cache short_code → long_url in Redis
- Cache-aside on redirect miss
- TTL aligned with link expiration',
       'Stateless API replicas behind an L7 load balancer.',
       'Async click events to Kafka/SQS for analytics to keep redirect path fast.',
       '- Hashing vs counters for key gen (collision handling vs coordination)
- 301 vs 302 (browser caching vs analytics accuracy)
- SQL vs NoSQL for mappings',
       'Hot short links, key-generation contention, DB write spikes during campaigns.',
       'Optimize the redirect path first; keep key generation simple and collision-safe.',
       'Start with requirements, estimate QPS, then choose encoding + storage.'
FROM system_design_problems p
WHERE p.title = 'Design a URL Shortener'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'hashing'
FROM system_design_problems p
WHERE p.title = 'Design a URL Shortener'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'redirects'
FROM system_design_problems p
WHERE p.title = 'Design a URL Shortener'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'scalability'
FROM system_design_problems p
WHERE p.title = 'Design a URL Shortener'
ON CONFLICT DO NOTHING;

-- Design Twitter / X
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design Twitter / X',
    'SOCIAL_NETWORKS',
    'HARD',
    'Design a Twitter-like microblogging system.

Users can post tweets, follow others, and view a home timeline of tweets from people they follow.
Support mentions, media uploads (optional), and high fan-out for celebrity accounts.',
    'Classic: Design Twitter',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design Twitter / X'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'A social feed system centered on write tweets, follow graph, and personalized home timelines.',
       '- Post a tweet
- Follow / unfollow users
- Home timeline (tweets from followees)
- User profile timeline
- Optional: likes, retweets, media',
       '- Timeline reads must feel instant
- High write volume for tweets
- Eventual consistency acceptable for feeds
- Availability over strict consistency for timelines',
       '- Read:write ratio heavily skewed to reads
- Most users have few followers; some celebrities have millions
- Tweets are immutable after post (edits optional/rare)',
       'Clients → API Gateway → Tweet Service / User Service / Timeline Service
Fan-out workers + queues
Tweet store, social graph store, timeline cache',
       '- Tweet service
- Social graph service
- Timeline / feed service
- Media service (optional)
- Fan-out workers
- Search/indexing (optional)',
       '- tweets(id, author_id, text, created_at, media_refs)
- follows(follower_id, followee_id)
- timelines(user_id, tweet_ids ordered) — cached / precomputed
Partition tweets by time or author.',
       'POST /tweets
POST /users/{id}/follow
GET /timeline/home?cursor=
GET /users/{id}/tweets',
       'Hybrid fan-out:
- Push fan-out for normal users
- Pull on read for celebrities / high-follower accounts
Shard by user_id for timelines and graphs.',
       'Redis/Memcached for home timelines; cache tweet objects; CDN for media.',
       'Stateless services behind LB; sticky sessions not required.',
       'Tweet-created events → fan-out queue → timeline writers.',
       'Push vs pull fan-out; precompute vs on-demand aggregation; consistency of feeds.',
       'Celebrity fan-out, hot timelines, write amplification.',
       'Call out hybrid fan-out early; quantify followers and QPS.',
       'Draw follow graph + timeline storage before diving into infra.'
FROM system_design_problems p
WHERE p.title = 'Design Twitter / X'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'feed'
FROM system_design_problems p
WHERE p.title = 'Design Twitter / X'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'fanout'
FROM system_design_problems p
WHERE p.title = 'Design Twitter / X'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'timeline'
FROM system_design_problems p
WHERE p.title = 'Design Twitter / X'
ON CONFLICT DO NOTHING;

-- Design Instagram
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design Instagram',
    'SOCIAL_NETWORKS',
    'HARD',
    'Design Instagram-like photo sharing.

Users upload photos/videos, follow creators, and browse a personalized feed plus explore/discover content.
Emphasize media storage, CDN delivery, and feed generation.',
    'Classic: Design Instagram',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design Instagram'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Photo/video social network with media-heavy writes and a personalized feed.',
       '- Upload photo/video posts
- Follow users
- Home feed
- Like / comment
- Explore / discover (simplified)',
       '- Fast media delivery worldwide
- Durable media storage
- Feed freshness with eventual consistency OK',
       'Media objects are large; metadata is small. Feed is more important than perfect ranking initially.',
       'Client → API → Post/Metadata services
Upload → object storage (S3) → CDN
Feed service with fan-out / ranking workers',
       'Upload service, media processor (thumbnails), post service, feed service, CDN, object store.',
       'posts, media_assets, follows, feed_entries; store media blobs in object storage, not DB.',
       'POST /posts (init upload + metadata), GET /feed, POST /posts/{id}/like',
       'CDN for media; shard metadata by user; async processing for transforms.',
       'CDN edge cache for media; Redis for feed pages and hot posts.',
       'Separate upload path from feed read path.',
       'Upload-complete → processing queue → feed fan-out.',
       'Precompute feed vs rank-on-read; image quality vs storage cost.',
       'Large uploads, CDN origin load, viral posts.',
       'Separate media plane from metadata plane early.',
       'Mention resumable uploads and thumbnail pipeline.'
FROM system_design_problems p
WHERE p.title = 'Design Instagram'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'media'
FROM system_design_problems p
WHERE p.title = 'Design Instagram'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'cdn'
FROM system_design_problems p
WHERE p.title = 'Design Instagram'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'feed'
FROM system_design_problems p
WHERE p.title = 'Design Instagram'
ON CONFLICT DO NOTHING;

-- Design a Chat System (WhatsApp)
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Chat System (WhatsApp)',
    'MESSAGING',
    'HARD',
    'Design a one-to-one and group messaging system like WhatsApp.

Support online/offline delivery, message ordering, read receipts, and media messages.
Focus on real-time delivery, presence, and durability.',
    'Classic: Design WhatsApp / Messenger',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Chat System (WhatsApp)'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Real-time messaging with durable storage, presence, and reliable delivery semantics.',
       '- 1:1 chat
- Group chat
- Online/offline delivery
- Delivery + read receipts
- Media messages (simplified)',
       '- Low latency for online users
- No message loss
- High concurrency connections
- Ordering per conversation (at least)',
       'Clients use WebSocket/long-poll. Messages are relatively small except media refs.',
       'Clients ↔ Connection gateways ↔ Chat service
Message store + inbox queues
Presence service
Push notification service for offline users',
       'Gateway (WS), chat service, presence, message DB, push notifier, media store.',
       'conversations, members, messages(conversation_id, seq, sender, body, created_at)
Sequence numbers per conversation for ordering.',
       'WS events: send_message, ack, receipt; REST for history: GET /conversations/{id}/messages',
       'Shard conversations; sticky WS connections to gateways; fan-out within group members.',
       'Cache recent conversation history; presence in Redis.',
       'L4/L7 for gateways with connection-aware routing.',
       'Internal queues for offline fan-out and push.',
       'Exactly-once vs at-least-once; group fan-out fan-in; SQL vs wide-column for messages.',
       'Large groups, gateway connection count, unread sync storms.',
       'Define delivery semantics and ordering before picking brokers.',
       'Draw online vs offline paths clearly.'
FROM system_design_problems p
WHERE p.title = 'Design a Chat System (WhatsApp)'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'websocket'
FROM system_design_problems p
WHERE p.title = 'Design a Chat System (WhatsApp)'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'presence'
FROM system_design_problems p
WHERE p.title = 'Design a Chat System (WhatsApp)'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'delivery'
FROM system_design_problems p
WHERE p.title = 'Design a Chat System (WhatsApp)'
ON CONFLICT DO NOTHING;

-- Design YouTube
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design YouTube',
    'STREAMING',
    'HARD',
    'Design a video streaming platform like YouTube.

Users upload videos, browse/search, and stream playback with adaptive bitrate.
Cover upload pipeline, encoding, CDN, and recommendations at a high level.',
    'Classic: Design YouTube / Netflix (video)',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design YouTube'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Video platform with heavy upload processing and global streaming delivery.',
       '- Upload video
- Process multiple resolutions
- Stream playback (ABR)
- Like/comment/subscribe (simplified)
- Search / recommendations (high level)',
       '- Global low-latency streaming
- High durability of originals
- Encoding throughput scalability',
       'Playback >> uploads. Viewers are globally distributed.',
       'Upload → object storage → encoding workers → packaged renditions → CDN
Metadata DB + search index
Playback API + CDN edge',
       'Upload service, transcoder fleet, metadata service, CDN, recommendation service.',
       'videos metadata, renditions, channels, view_events (analytics pipeline).',
       'POST /videos/upload-session, GET /videos/{id}, GET /videos/{id}/manifest.m3u8',
       'Autoscaled encoders; CDN for almost all bytes; shard metadata.',
       'CDN caches segments aggressively; cache metadata for hot videos.',
       'Separate upload, API, and streaming origins.',
       'Upload-complete → encoding jobs; view events → analytics stream.',
       'Encoding cost vs quality/latency; push vs pull CDN; consistency of view counts.',
       'Transcoding backlog, viral video origin pressure, search ranking.',
       'Treat video bytes and metadata as different systems.',
       'Mention HLS/DASH and ABR briefly.'
FROM system_design_problems p
WHERE p.title = 'Design YouTube'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'video'
FROM system_design_problems p
WHERE p.title = 'Design YouTube'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'encoding'
FROM system_design_problems p
WHERE p.title = 'Design YouTube'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'cdn'
FROM system_design_problems p
WHERE p.title = 'Design YouTube'
ON CONFLICT DO NOTHING;

-- Design Uber
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design Uber',
    'SCHEDULING',
    'HARD',
    'Design a ride-hailing system like Uber.

Riders request rides; the system matches nearby drivers, tracks live location, pricing, and trip lifecycle.
Emphasize geospatial matching and real-time updates.',
    'Classic: Design Uber / Lyft',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design Uber'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Marketplace matching riders and drivers with geospatial indexing and live trip state.',
       '- Request a ride
- Match nearby drivers
- Live location updates
- Trip states (accepted → arrived → ongoing → completed)
- Payments (high level)',
       '- Matching latency of a few seconds
- High availability in cities
- Location update throughput',
       'Supply/demand varies by city/time. GPS updates every few seconds.',
       'Rider/Driver apps → API → Trip service + Matching service
Location service with geo index (quadtree/geohash/Redis GEO)
Dispatch + notifications',
       'Location service, matching/dispatch, trip service, pricing, maps/ETA, payments.',
       'drivers_location (ephemeral), trips, users, payments; geo index separate from durable trip store.',
       'POST /trips, WS location updates, POST /trips/{id}/accept, GET /trips/{id}',
       'Partition by city/region; isolate hot cities; async matching retries.',
       'Cache nearby drivers in memory/Redis GEO; ETA caches.',
       'Regional deployments; route users to nearest region.',
       'Trip events to drivers via push/WS; location stream processing.',
       'Push vs pull matching; consistency of driver availability; surge pricing complexity.',
       'Location write QPS, matching in dense downtowns, map ETA dependency.',
       'Lead with geo data model and regionalization.',
       'Clarify matching radius and SLA early.'
FROM system_design_problems p
WHERE p.title = 'Design Uber'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'geo'
FROM system_design_problems p
WHERE p.title = 'Design Uber'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'matching'
FROM system_design_problems p
WHERE p.title = 'Design Uber'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'realtime'
FROM system_design_problems p
WHERE p.title = 'Design Uber'
ON CONFLICT DO NOTHING;

-- Design Dropbox / Google Drive
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design Dropbox / Google Drive',
    'STORAGE',
    'HARD',
    'Design a cloud file storage and sync system.

Users upload files/folders, sync across devices, share links, and version files.
Focus on chunking, deduplication, metadata vs blob separation, and sync protocol.',
    'Classic: Design Dropbox',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design Dropbox / Google Drive'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Reliable file sync with block-level transfer and strong metadata consistency needs.',
       '- Upload / download files
- Sync across devices
- Folder hierarchy
- Sharing links / ACLs (simplified)
- Version history (basic)',
       '- Efficient sync (only changed blocks)
- Durability of file content
- Conflict handling for concurrent edits',
       'Many small metadata ops; large binary payloads go to object storage.',
       'Clients → Sync service / Metadata service
Block/chunk store in object storage
Notification service for sync events',
       'Metadata DB, block server, notification service, client sync engine.',
       'namespaces, files/folders metadata, block hashes, versions, sharing ACLs.',
       'POST /blocks, POST /files/commit, GET /delta?cursor=, GET /files/{id}',
       'Metadata sharding by namespace; object storage horizontal; client-side chunking.',
       'Cache metadata trees; local disk cache on clients.',
       'Separate metadata control plane from block data plane.',
       'Change notifications to connected devices.',
       'Sync conflicts (last-write-wins vs CRDT); dedupe savings vs CPU; consistency.',
       'Metadata hotspots, large folder listings, simultaneous device sync.',
       'Never store file bytes in the metadata DB.',
       'Explain chunking + content-hash dedupe.'
FROM system_design_problems p
WHERE p.title = 'Design Dropbox / Google Drive'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'sync'
FROM system_design_problems p
WHERE p.title = 'Design Dropbox / Google Drive'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'chunking'
FROM system_design_problems p
WHERE p.title = 'Design Dropbox / Google Drive'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'object-storage'
FROM system_design_problems p
WHERE p.title = 'Design Dropbox / Google Drive'
ON CONFLICT DO NOTHING;

-- Design a Rate Limiter
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Rate Limiter',
    'DISTRIBUTED_SYSTEMS',
    'MEDIUM',
    'Design a distributed rate limiter used by an API gateway.

Limit requests per user/IP/API key (e.g., 100 req/min). Support multiple algorithms and deployment across many gateway nodes.',
    'Classic: Design Rate Limiter',
    45,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Rate Limiter'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Protect backend services by enforcing quotas consistently across a distributed edge.',
       '- Allow/deny a request given a key and rule
- Support per-user / per-IP / per-endpoint rules
- Return remaining quota / retry-after',
       '- Very low overhead on the request path
- High accuracy under concurrency
- Highly available (fail open vs fail closed decision)',
       'Called on every API request. Rules can be updated dynamically.',
       'API Gateway → Rate limiter library/sidecar → Redis (or local + sync) → backend',
       'Rule config service, limiter counters store, gateway integration, metrics.',
       'Rules in config DB; counters in Redis with TTLs / sorted sets depending on algorithm.',
       'internal check(key, cost) → { allowed, remaining, resetAt }; HTTP 429 when denied.',
       'Shard Redis by key; local token buckets with periodic sync for softer limits.',
       'Cache compiled rules in each gateway process.',
       'Stateless gateways; centralized counter store for hard limits.',
       'Optional async rule updates via pub/sub.',
       'Token bucket vs sliding window vs fixed window; accuracy vs performance; fail open/closed.',
       'Redis hot keys for popular users; clock skew.',
       'State the algorithm and consistency needs up front.',
       'Compare fixed window burstiness vs sliding window cost.'
FROM system_design_problems p
WHERE p.title = 'Design a Rate Limiter'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'token-bucket'
FROM system_design_problems p
WHERE p.title = 'Design a Rate Limiter'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'redis'
FROM system_design_problems p
WHERE p.title = 'Design a Rate Limiter'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'gateway'
FROM system_design_problems p
WHERE p.title = 'Design a Rate Limiter'
ON CONFLICT DO NOTHING;

-- Design a Web Crawler
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Web Crawler',
    'SEARCH',
    'HARD',
    'Design a distributed web crawler.

Given seed URLs, discover links, fetch pages politely, and store content for indexing.
Handle duplicates, robots.txt, politeness, and continuous recrawl.',
    'Classic: Design Web Crawler',
    50,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Web Crawler'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Distributed fetcher that maintains a URL frontier and produces crawl outputs for search indexing.',
       '- Enqueue seed URLs
- Fetch pages
- Extract links
- Respect robots.txt / politeness
- Store raw content / metadata',
       '- High throughput crawl
- Avoid overwhelming target sites
- Fault tolerant workers',
       'WWW is huge; freshness requirements vary by site importance.',
       'URL frontier → DNS resolver → fetcher workers → content store + link extractor → back to frontier',
       'Frontier queue, politeness scheduler, fetchers, content store, dedupe (URL/content hash).',
       'url_seen bloom/DB, crawl_metadata, raw_docs in object storage.',
       'Admin: POST /seeds, GET /stats; workers pull tasks from frontier.',
       'Partition frontier by host hash; scale fetcher fleet independently.',
       'Cache DNS and robots.txt per host.',
       'Host-aware scheduling to enforce politeness.',
       'Frontier queues (Kafka/SQS) for URL tasks.',
       'BFS vs priority crawl; exact vs probabilistic dedupe; politeness vs speed.',
       'DNS, slow hosts, frontier size, duplicate URL explosion.',
       'Politeness and dedupe dominate real crawler design.',
       'Mention bloom filters for URL seen set.'
FROM system_design_problems p
WHERE p.title = 'Design a Web Crawler'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'crawler'
FROM system_design_problems p
WHERE p.title = 'Design a Web Crawler'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'frontier'
FROM system_design_problems p
WHERE p.title = 'Design a Web Crawler'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'politeness'
FROM system_design_problems p
WHERE p.title = 'Design a Web Crawler'
ON CONFLICT DO NOTHING;

-- Design a Notification System
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Notification System',
    'NOTIFICATIONS',
    'MEDIUM',
    'Design a multi-channel notification system.

Send push, email, and SMS notifications triggered by product events, with user preferences, templating, and retry/backoff.',
    'Classic: Notification Service',
    45,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Notification System'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Event-driven notification pipeline with preferences, templates, and channel adapters.',
       '- Trigger notifications from events
- User preference / quiet hours
- Templates per channel
- Delivery retries
- Basic delivery status',
       '- High throughput bursts
- At-least-once delivery with idempotency
- Low latency for critical alerts',
       'Providers (APNs/FCM/SES/Twilio) are external. Some notifications are priority.',
       'Event bus → notification service → preference check → channel workers → providers',
       'Ingest API, template service, preference store, channel workers, provider adapters, status store.',
       'users_preferences, templates, notification_outbox, delivery_attempts.',
       'POST /notifications {userId, template, data, channels?}',
       'Partition workers by channel; priority queues for urgent alerts.',
       'Cache templates and preferences.',
       'Consumer groups on the event bus.',
       'Kafka/SQS between ingest and channel workers.',
       'Push vs pull preferences; sync vs async send; fan-out cost.',
       'Provider rate limits, template render CPU, burst events.',
       'Idempotency keys prevent duplicate pushes on retries.',
       'Draw channel adapters as pluggable.'
FROM system_design_problems p
WHERE p.title = 'Design a Notification System'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'push'
FROM system_design_problems p
WHERE p.title = 'Design a Notification System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'email'
FROM system_design_problems p
WHERE p.title = 'Design a Notification System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'fanout'
FROM system_design_problems p
WHERE p.title = 'Design a Notification System'
ON CONFLICT DO NOTHING;

-- Design Typeahead / Autocomplete
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design Typeahead / Autocomplete',
    'SEARCH',
    'MEDIUM',
    'Design a search autocomplete / typeahead system.

As the user types, return top suggested queries ranked by popularity and personalization (optional).
Optimize for ultra-low latency.',
    'Classic: Design Autocomplete',
    45,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design Typeahead / Autocomplete'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Prefix suggestion service optimized for low-latency reads and frequent ranking updates.',
       '- Return top-K suggestions for a prefix
- Update suggestion frequencies from search logs
- Optional personalization / trending',
       '- p99 latency of tens of milliseconds
- High QPS
- Eventual consistency for ranking updates OK',
       'English-first; limited result set (K≈10).',
       'Client → Autocomplete API → in-memory trie/prefix index (+ cache) ← offline aggregator from logs',
       'Query service, trie/shards, analytics aggregator, personalization (optional).',
       'Aggregated query frequencies; periodically rebuilt prefix index snapshots.',
       'GET /suggest?q=pre&limit=10',
       'Shard tries by prefix; replicate read-only indexes on each node.',
       'Cache hot prefixes; CDN/edge for anonymous popular prefixes.',
       'Stateless suggest nodes with local indexes.',
       'Search log stream → frequency aggregator.',
       'Trie vs inverted index; online updates vs periodic rebuilds.',
       'Hot prefixes, index memory size, personalization fan-out.',
       'Most of the win is an in-memory prefix structure + caching.',
       'Discuss AJAX debounce on client briefly.'
FROM system_design_problems p
WHERE p.title = 'Design Typeahead / Autocomplete'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'trie'
FROM system_design_problems p
WHERE p.title = 'Design Typeahead / Autocomplete'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'prefix'
FROM system_design_problems p
WHERE p.title = 'Design Typeahead / Autocomplete'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'ranking'
FROM system_design_problems p
WHERE p.title = 'Design Typeahead / Autocomplete'
ON CONFLICT DO NOTHING;

-- Design a Distributed Cache
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Distributed Cache',
    'CACHING',
    'HARD',
    'Design a distributed in-memory cache like Redis/Memcached.

Support get/set with TTLs, eviction, replication/sharding, and client routing.
Discuss consistency and failure handling.',
    'Classic: Design Distributed Cache',
    50,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Distributed Cache'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Low-latency memory store used as a shared cache across application servers.',
       '- GET / SET / DELETE
- TTL expiration
- Eviction (LRU/LFU)
- Clustered deployment',
       '- Sub-millisecond local latency goals
- High availability via replication (optional)
- Predictable memory usage',
       'Cache is not source of truth; DB remains authoritative.',
       'App clients → cache proxy/client → shard nodes (consistent hashing) → optional replicas',
       'Cache nodes, cluster manager, client library, metrics.',
       'In-memory hash table + eviction structures; optional AOF/RDB persistence if Redis-like.',
       'GET key, SET key value EX seconds, DEL key; RESP or HTTP internal API.',
       'Consistent hashing with virtual nodes; reshard on membership change.',
       'N/A (this is the cache); discuss stampede prevention (locks/probabilistic early expire).',
       'Client-side hashing preferred over centralized LB for data locality.',
       'Gossip/membership for cluster state.',
       'CP vs AP for replicas; memory vs hit rate; persistence vs pure cache.',
       'Hot keys, network, GC/memory fragmentation.',
       'Hot-key mitigation (local cache, replication) is a common follow-up.',
       'Bring consistent hashing drawing.'
FROM system_design_problems p
WHERE p.title = 'Design a Distributed Cache'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'redis'
FROM system_design_problems p
WHERE p.title = 'Design a Distributed Cache'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'consistent-hashing'
FROM system_design_problems p
WHERE p.title = 'Design a Distributed Cache'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'eviction'
FROM system_design_problems p
WHERE p.title = 'Design a Distributed Cache'
ON CONFLICT DO NOTHING;

-- Design a Key-Value Store
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Key-Value Store',
    'DATABASES',
    'HARD',
    'Design a distributed key-value store (Dynamo-style).

Provide durable get/put with tunable consistency, replication, and partition tolerance.
Cover hashing, quorum, hinted handoff, and anti-entropy at a high level.',
    'Classic: Design KV Store / Dynamo',
    60,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Key-Value Store'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Wide-area durable KV store inspired by Amazon Dynamo / Cassandra principles.',
       '- Put(key, value)
- Get(key)
- Configurable replication factor',
       '- High availability
- Horizontal scale
- Tunable consistency (N, W, R)',
       'Keys are opaque; values are blobs; conflicts may occur under partitions.',
       'Client → coordinator → replica nodes on consistent-hash ring',
       'Coordinator, storage engine (LSM/B-Tree), membership, repair (read repair / Merkle).',
       'Partitioned key ranges; commit logs; SSTables/memtables if LSM.',
       'PUT /kv/{key}, GET /kv/{key}?consistency=quorum',
       'Add nodes → rebalance tokens; virtual nodes for even load.',
       'Optional page cache; not primary focus.',
       'Request coordination with preference lists.',
       'Anti-entropy and hinted handoff channels.',
       'Consistency vs availability; read repair vs background sync; LSM vs B-tree.',
       'Hot partitions, compaction, coordinator CPU.',
       'Explain N/W/R clearly with an example.',
       'Vector clocks / last-write-wins for conflicts.'
FROM system_design_problems p
WHERE p.title = 'Design a Key-Value Store'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'dynamo'
FROM system_design_problems p
WHERE p.title = 'Design a Key-Value Store'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'quorum'
FROM system_design_problems p
WHERE p.title = 'Design a Key-Value Store'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'partitioning'
FROM system_design_problems p
WHERE p.title = 'Design a Key-Value Store'
ON CONFLICT DO NOTHING;

-- Design a News Feed System
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a News Feed System',
    'SOCIAL_NETWORKS',
    'HARD',
    'Design a news feed for a social network.

Generate a personalized feed of posts from followed entities with ranking, pagination, and near-real-time freshness.',
    'Classic: News Feed',
    55,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a News Feed System'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Feed generation combining graph relationships, ranking signals, and efficient retrieval.',
       '- Publish post
- Follow entities
- Fetch ranked home feed with pagination',
       '- Fast first-page load
- Freshness within seconds/minutes
- Scale to large graphs',
       'Similar to Twitter/Instagram feed but ranking may be more prominent.',
       'Post service → fan-out/rank workers → feed store/cache ← Feed API',
       'Post service, graph service, ranking service, feed cache, media CDN.',
       'posts, follows, feed_cache(user_id → ranked post ids).',
       'GET /feed?cursor= ; POST /posts',
       'Hybrid fan-out; pre-rank top pages; shard feed caches.',
       'Materialized first N pages in Redis.',
       'Stateless feed API replicas.',
       'Post-created events drive fan-out and ranking.',
       'Relevance vs latency; precompute vs online rank; fairness vs engagement.',
       'Celebrity publishers, ranking feature retrieval, cache stampedes.',
       'Separate retrieval candidate generation from ranking.',
       'Mention cursor pagination, not offset.'
FROM system_design_problems p
WHERE p.title = 'Design a News Feed System'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'feed'
FROM system_design_problems p
WHERE p.title = 'Design a News Feed System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'ranking'
FROM system_design_problems p
WHERE p.title = 'Design a News Feed System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'fanout'
FROM system_design_problems p
WHERE p.title = 'Design a News Feed System'
ON CONFLICT DO NOTHING;

-- Design Ticketmaster
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design Ticketmaster',
    'SCHEDULING',
    'HARD',
    'Design a ticket booking system like Ticketmaster.

Users browse events, select seats, hold inventory briefly, and complete payment before tickets are confirmed.
Handle flash sales and oversell prevention.',
    'Classic: Design Ticketmaster',
    55,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design Ticketmaster'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'High-contention inventory reservation system with time-boxed holds and payment confirmation.',
       '- Browse events / seats
- Hold seats for a limited time
- Checkout + payment
- Confirm or release inventory',
       '- No double booking
- Survive traffic spikes at onsale
- Fair-enough allocation under load',
       'Seat-level inventory for venues; some GA (general admission) events too.',
       'Client → Event catalog → Seat map service → Reservation service → Payment → Ticketing',
       'Inventory service, reservation/hold service, payment orchestrator, queue/waiting room.',
       'events, seats, holds(seat_id, user_id, expires_at), orders; strong consistency on seat state.',
       'POST /holds, POST /checkout, webhook payment confirmation.',
       'Waiting room / virtual queue; shard by event; isolate hot events.',
       'Cache seat maps; do not cache authoritative availability without versioning.',
       'Queue users before hitting booking tier.',
       'Hold expiration workers; payment events.',
       'Pessimistic locks vs conditional updates; overbooking buffer vs strict inventory.',
       'Hot event seat rows, payment latency holding inventory.',
       'Time-boxed holds + idempotent checkout are core.',
       'Discuss waiting room as first line of defense.'
FROM system_design_problems p
WHERE p.title = 'Design Ticketmaster'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'inventory'
FROM system_design_problems p
WHERE p.title = 'Design Ticketmaster'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'locking'
FROM system_design_problems p
WHERE p.title = 'Design Ticketmaster'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'payments'
FROM system_design_problems p
WHERE p.title = 'Design Ticketmaster'
ON CONFLICT DO NOTHING;

-- Design a Payment System
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Payment System',
    'PAYMENTS',
    'HARD',
    'Design an online payments / checkout service.

Support authorizing and capturing charges, idempotent APIs, ledgering, refunds, and reconciliation with payment processors.',
    'Classic: Payment System',
    55,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Payment System'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Financially correct payment orchestration with an immutable ledger and strong idempotency.',
       '- Create payment intent / charge
- Capture / cancel
- Refunds
- Webhooks from processors
- Merchant dashboards (high level)',
       '- Exactly-once business effect despite retries
- Auditability
- High security / PCI considerations',
       'Card data tokenized by processor (Stripe-like); you store tokens, not PANs.',
       'Merchant API → Payment service → Ledger → Processor adapters; webhook handlers',
       'API, ledger, risk checks, processor connectors, reconciliation jobs.',
       'payments, ledger_entries (double-entry), refunds, idempotency_keys.',
       'POST /v1/payments (Idempotency-Key), POST /v1/payments/{id}/refund',
       'Partition by merchant; async webhooks; careful serialization per payment id.',
       'Minimal caching of financial state; cache merchant config only.',
       'Stateless API; sticky not required if state in DB.',
       'Outbox for processor calls and webhook processing.',
       'Sync auth vs async capture; strong ledger consistency vs throughput.',
       'Processor latency, reconciliation gaps, hotspot merchants.',
       'Idempotency keys + ledger are non-negotiable.',
       'Mention PCI scope reduction via tokenization.'
FROM system_design_problems p
WHERE p.title = 'Design a Payment System'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'ledger'
FROM system_design_problems p
WHERE p.title = 'Design a Payment System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'idempotency'
FROM system_design_problems p
WHERE p.title = 'Design a Payment System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'pci'
FROM system_design_problems p
WHERE p.title = 'Design a Payment System'
ON CONFLICT DO NOTHING;

-- Design a CDN
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a CDN',
    'CDN',
    'HARD',
    'Design a Content Delivery Network.

Cache static (and optionally dynamic) content at edge PoPs close to users, with origin shield, invalidation, and cache hierarchy.',
    'Classic: Design CDN',
    50,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a CDN'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Globally distributed caching layer that reduces latency and origin load.',
       '- Resolve user to nearest PoP
- Cache GET content at edge
- Origin fetch on miss
- Purge/invalidate content',
       '- High cache hit ratio
- Low TTFB worldwide
- Fast invalidation propagation',
       'Primarily static assets first; HTTPS termination at edge.',
       'DNS/Anycast → Edge PoP → Regional/Origin shield → Origin',
       'DNS/traffic steering, edge caches, origin shield, control plane for purge, logging.',
       'Control-plane config; cache is ephemeral storage (SSD/memory) keyed by URL+vary headers.',
       'Purge API: POST /purge { urls/tags }; config APIs for cache rules.',
       'Add PoPs; hierarchical caching; Anycast routing.',
       'TTL + validation (ETag); cache keys with query normalization; negative caching carefully.',
       'Anycast + health-checked PoPs.',
       'Purge fan-out to edges.',
       'TTL freshness vs hit rate; purge latency vs complexity; HTTPS cert management.',
       'Origin storms on misses; purge storms; hot objects.',
       'Origin shield dramatically reduces thundering herds.',
       'Draw PoP hierarchy.'
FROM system_design_problems p
WHERE p.title = 'Design a CDN'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'edge'
FROM system_design_problems p
WHERE p.title = 'Design a CDN'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'cache-hierarchy'
FROM system_design_problems p
WHERE p.title = 'Design a CDN'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'invalidation'
FROM system_design_problems p
WHERE p.title = 'Design a CDN'
ON CONFLICT DO NOTHING;

-- Design a Metrics / Monitoring System
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design a Metrics / Monitoring System',
    'ANALYTICS',
    'MEDIUM',
    'Design a metrics collection and monitoring platform (Datadog/Prometheus-like).

Ingest time-series metrics from many services, store efficiently, query/graph, and alert on thresholds.',
    'Classic: Design Metrics System',
    50,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design a Metrics / Monitoring System'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'High-cardinality time-series ingest, storage, query, and alerting pipeline.',
       '- Ingest metrics (counters/gauges/histograms)
- Query ranges with aggregation
- Dashboards
- Alert rules + notifications',
       '- Very high write QPS
- Efficient storage compression
- Query latency for dashboards',
       'Agents push or pull metrics; labels/tags create cardinality challenges.',
       'Agents → ingest gateway → write path (TSDB) → query API; rules engine → alerter',
       'Ingest, TSDB shards, query frontend, ruler/alerter, UI.',
       'Time-series shards by metric key + time; indexing for label sets.',
       'POST /write, GET /query_range, CRUD /alerts',
       'Shard by metric hash; downsampling older data; separate hot/cold storage.',
       'Cache recent query results and series metadata.',
       'Ingest gateways horizontally scaled.',
       'Optional Kafka buffer in front of writers.',
       'Push vs pull; cardinality limits; precision vs compression.',
       'High-cardinality explosions, compaction, alert evaluation load.',
       'Call out cardinality as the hard part.',
       'Mention histograms/percentiles briefly.'
FROM system_design_problems p
WHERE p.title = 'Design a Metrics / Monitoring System'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'timeseries'
FROM system_design_problems p
WHERE p.title = 'Design a Metrics / Monitoring System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'ingestion'
FROM system_design_problems p
WHERE p.title = 'Design a Metrics / Monitoring System'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'alerting'
FROM system_design_problems p
WHERE p.title = 'Design a Metrics / Monitoring System'
ON CONFLICT DO NOTHING;

-- Design an API Rate-Limited Gateway / Auth Service
INSERT INTO system_design_problems (
    title, category, difficulty, description, original_source,
    estimated_interview_time, favorite, created_at, updated_at
)
SELECT
    'Design an API Rate-Limited Gateway / Auth Service',
    'SECURITY',
    'MEDIUM',
    'Design an API gateway with authentication and authorization.

Terminate TLS, authenticate clients (API keys/JWT/OAuth), authorize routes, apply rate limits, and route to upstream services.',
    'Classic: API Gateway',
    45,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_problems sdp WHERE sdp.title = 'Design an API Rate-Limited Gateway / Auth Service'
);

INSERT INTO system_design_documents (
    problem_id, overview, functional_requirements, non_functional_requirements,
    assumptions, high_level_architecture, components, database_design, api_design,
    scaling_strategy, caching_strategy, load_balancing, messaging, tradeoffs,
    bottlenecks, lessons_learned, personal_notes
)
SELECT p.id,
       'Edge control plane for authn/authz, routing, and cross-cutting policies.',
       '- Authenticate requests
- Authorize scopes/roles
- Route to upstreams
- Rate limit / WAF basics
- Request logging',
       '- Low added latency
- High availability
- Safe key rotation',
       'Upstream services are microservices; gateway is the only public entry.',
       'Client → Gateway (auth + policies) → service mesh / upstreams; Auth/config control plane',
       'Gateway data plane, auth service, config distribution, rate limiter, observability.',
       'clients/apps, keys, policies; keys hashed at rest.',
       'OAuth token endpoint; admin APIs for apps/policies.',
       'Horizontal gateway replicas; cache JWKS/keys; regional gateways.',
       'Cache validated tokens/JWKS with short TTL; policy snapshots.',
       'LB in front of gateway fleet.',
       'Config push (xDS-like) to gateways.',
       'Gateway monolith vs per-service sidecars; JWT validation locality vs central introspection.',
       'Auth service dependency, large header transforms, hot tenants.',
       'Keep data plane lean; push policy locally.',
       'Distinguish authn vs authz in the interview.'
FROM system_design_problems p
WHERE p.title = 'Design an API Rate-Limited Gateway / Auth Service'
  AND NOT EXISTS (SELECT 1 FROM system_design_documents d WHERE d.problem_id = p.id);

INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'auth'
FROM system_design_problems p
WHERE p.title = 'Design an API Rate-Limited Gateway / Auth Service'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'jwt'
FROM system_design_problems p
WHERE p.title = 'Design an API Rate-Limited Gateway / Auth Service'
ON CONFLICT DO NOTHING;
INSERT INTO system_design_tags (problem_id, tag)
SELECT p.id, 'gateway'
FROM system_design_problems p
WHERE p.title = 'Design an API Rate-Limited Gateway / Auth Service'
ON CONFLICT DO NOTHING;

COMMIT;
