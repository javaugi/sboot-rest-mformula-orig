ETag/LastModified vs Idempotency

🏁 Rule of Thumb
    Idempotency Key → network retries & duplicate suppression.
    ETag / Last-Modified → cache validation & concurrency control.
Use both in modern APIs:
    POST /resource with Idempotency Key
    GET/PUT /resource/{id} with ETag

Both are about safety and correctness, but they solve different problems in distributed systems and APIs. Let’s break it down carefully so you know exactly when to use each, how they interact, and I'll give you practical Spring Boot code.

🔹 ETag & Last-Modified: State Validation / Cache Revalidation
Goal:
    Prevent clients from acting on stale resource state (optimistic concurrency) and avoid sending unchanged data.
    ETag is a fingerprint of the current resource representation (e.g., version, hash).
    Last-Modified is a timestamp of last change.
    If-None-Match or If-Modified-Since:
        → GET: avoid sending unchanged body (server returns 304 Not Modified).
    If-Match or If-Unmodified-Since:
        → PUT/DELETE: avoid lost updates (server rejects if state changed since client last read).
Scope:
    Per-resource, state-based.
    When to use:
        Caching dynamic GET responses → reduces bandwidth.
        Optimistic locking on writes → prevents lost updates.

🔹 Idempotency Key: Safe Retries for Operations
Goal:
    Ensure multiple identical requests (e.g., due to network retries or client-side issues) produce the same effect exactly once.
    Typically a unique client-generated key (UUID) sent in header:
    Idempotency-Key: 123e4567-e89b-12d3-a456-426614174000
    Server stores this key with the request result for a time window.
    If the same key is reused → server returns same response (or at least no duplicate side effect).
Scope:
    Per-request operation, not per-resource.
When to use:
    Payments, orders, booking APIs where duplicates → $ cost or business errors.
    Any unsafe method (POST/PUT) with potential side-effects on retries.

✅ Key Differences Table
Feature             ETag / Last-Modified                                Idempotency Key
Purpose             Cache validation, optimistic concurrency            Prevent duplicate side-effects
Scope               Per-resource state                                  Per request/operation
Client supplies?	Not required (server sends ETag, client echoes)     Yes, client must send unique key
Used with           GET (304), PUT/DELETE (412)                         POST/PUT/DELETE (safe retries)
Server logic        Compare version/timestamp with current state        Store key+response, reuse if retried
Failure status      304 Not Modified, 412 Precondition Failed           409 Conflict or same 200 result on retry
Typical lifetime	Until resource changes                              TTL window (e.g., 24h)
⚡ How They Work Together

ETag ensures you don’t overwrite someone else’s change.
Idempotency Key ensures retrying the same operation doesn’t double charge or double create.
    For example:
        POST /payments with Idempotency-Key: xyz ensures only one payment.
        Later GET /payments/123 with If-None-Match: "v3" avoids downloading unchanged data.

🧩 Spring Boot Code Examples
1) Idempotency Key Middleware (e.g., for POST payments)
    @Component
    public class IdempotencyInterceptor implements HandlerInterceptor {

        private final Map<String, ResponseEntity<?>> cache = new ConcurrentHashMap<>();

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                String key = request.getHeader("Idempotency-Key");
                if (key != null && cache.containsKey(key)) {
                    ResponseEntity<?> cachedResponse = cache.get(key);
                    response.setStatus(cachedResponse.getStatusCodeValue());
                    // Write cached body if needed...
                    return false; // skip controller
                }
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                String key = request.getHeader("Idempotency-Key");
                if (key != null && !cache.containsKey(key)) {
                    // Simplified: store response code only; real impl needs response wrapper
                    cache.put(key, ResponseEntity.status(response.getStatus()).build());
                }
            }
        }
    }

2) ETag on GET + Idempotency on POST
    @RestController
    @RequestMapping("/orders")
    public class OrderController {
        private final OrderService service;

        @GetMapping("/{id}")
        public ResponseEntity<Order> getOrder(@PathVariable String id, HttpServletRequest req, HttpServletResponse resp) {
            Order order = service.findById(id);
            String eTag = "\"" + order.getVersion() + "\"";
            long lastModified = order.getUpdatedAt().toEpochMilli();

            ServletWebRequest webRequest = new ServletWebRequest(req, resp);
            if (webRequest.checkNotModified(eTag, lastModified)) {
                return ResponseEntity.status(304).eTag(eTag).lastModified(lastModified).build();
            }
            return ResponseEntity.ok().eTag(eTag).lastModified(lastModified).body(order);
        }

        @PostMapping
        public ResponseEntity<Order> createOrder(@RequestBody CreateOrderReq req,
                                                 @RequestHeader("Idempotency-Key") String key) {
            Order order = service.createOrderIfNotExists(key, req); // service caches key
            return ResponseEntity.status(201).eTag("\"" + order.getVersion() + "\"").body(order);
        }
    }


Those conditional request headers are powerful for caching, bandwidth savings, and safe concurrency control when updating resources. 
    Below I explain when to use each header, the HTTP semantics you must respect, practical tradeoffs, and multiple Spring Boot Java 
    examples you can drop into your app.

Quick summary (when to use)
    ETag / If-None-Match — preferred for precise cache validation. Use when you can generate a stable resource version (hash or DB version). 
        If-None-Match on GET → server returns 304 Not Modified if ETag matches. Also If-None-Match: "*" with PUT indicates “create only if not exists.”
    If-Match — optimistic concurrency control for updates (PUT/PATCH/DELETE). Client sends the ETag it last saw; server rejects (HTTP 412 
        Precondition Failed) if the current ETag doesn’t match.
    Last-Modified / If-Modified-Since — simpler time-based cache validation. Use when you track a resource last-modified timestamp. Less precise 
        than ETag (coarser granularity, date-based).
    If-Unmodified-Since — like If-Match, but time-based: proceed only if resource unchanged since given date, otherwise fail.
    Precedence: If both If-None-Match and If-Modified-Since are present, If-None-Match takes precedence (per RFC).

Strong vs Weak ETag
    Strong ETag (no prefix): byte-for-byte match required — use when exact content equality matters.
    Weak ETag (prefix W/): semantically equivalent content, but not byte-equal (e.g., different JSON field order). Use when you want to allow some 
        minor differences but still treat as “same” for caching.
HTTP status codes to know
    304 Not Modified — resource unchanged; no body returned for GET/HEAD.
    412 Precondition Failed — precondition (If-Match / If-Unmodified-Since) failed.
    201 Created / 200 OK — successful create/update.
    428 Precondition Required — optional: server requires conditional request (used to avoid lost updates).
Practical patterns & recommendations
    Prefer ETag for most dynamic resources — exactly identifies a representation/version.
    If you have a DB version field (@Version), use that to compute ETag (cheap).
    If you must hash response body to generate ETag, beware of CPU/memory cost for large payloads.
    Provide both ETag and Last-Modified — clients/caches can use either; server should respect If-None-Match first.
    Use If-Match on unsafe operations (PUT/DELETE) to enforce optimistic concurrency.
    Support If-None-Match: "*" on PUT to implement create-only-if-not-exists semantics.
    Avoid ShallowEtagHeaderFilter for very large responses or streaming responses — it buffers to compute ETag.

Spring Boot examples
Below are small, copy-pasteable examples for common use-cases.
1) Simple GET with ETag (computed from DB version) + Last-Modified
    Use when your JPA entity has a @Version or updatedAt timestamp.

        // Order entity (excerpt)
        @Entity
        public class Order {
            @Id
            private String id;

            @Version
            private Long version; // ideal for ETag

            private Instant updatedAt; // for Last-Modified
            // ... getters/setters ...
        }

        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.context.request.ServletWebRequest;

        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import java.time.Instant;

        @RestController
        @RequestMapping("/orders")
        public class OrderController {

            private final OrderRepo orderRepo;

            public OrderController(OrderRepo orderRepo) { this.orderRepo = orderRepo; }

            @GetMapping("/{id}")
            public ResponseEntity<OrderDto> getOrder(@PathVariable String id,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
                Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException());

                // 1) Build a cheap ETag from DB version
                String eTag = "\"" + order.getVersion() + "\"";

                // 2) Last-Modified from updatedAt
                long lastModified = order.getUpdatedAt().toEpochMilli();

                // 3) Let Spring helper check If-None-Match / If-Modified-Since
                ServletWebRequest webRequest = new ServletWebRequest(request, response);
                // checkNotModified returns true if the request is conditional and NOT modified
                if (webRequest.checkNotModified(eTag, lastModified)) {
                    // ServletWebRequest already set response status to 304
                    return ResponseEntity.status(304).eTag(eTag).lastModified(lastModified).build();
                }

                // 4) Return full resource with caching headers
                return ResponseEntity.ok()
                        .eTag(eTag)
                        .lastModified(lastModified)
                        .cacheControl(org.springframework.http.CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS).cachePublic())
                        .body(OrderDto.from(order));
            }
        }


Notes:
    ServletWebRequest.checkNotModified(eTag, lastModified) inspects If-None-Match and If-Modified-Since and returns true if not modified.
    Using DB @Version for ETag is cheap (no hashing).

2) Conditional Update (PUT) using If-Match for optimistic locking
    Client reads resource, stores ETag, then sends If-Match: "v" with update.
        @PutMapping("/{id}")
        public ResponseEntity<?> updateOrder(@PathVariable String id,
                                             @RequestHeader(value = "If-Match", required = false) String ifMatch,
                                             @RequestBody UpdateOrderRequest req) {
            Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException());
            String currentEtag = "\"" + order.getVersion() + "\"";

            if (ifMatch == null) {
                // You may reject non-conditional updates with 428 or proceed depending on policy
                // return ResponseEntity.status(428).body("Precondition required (send If-Match)");
            }

            // If-Match can be a list of ETags. Simple compare:
            if (ifMatch != null && !ifMatch.equals(currentEtag) && !ifMatch.equals("*")) {
                // Precondition failed — resource changed since client last saw it
                return ResponseEntity.status(412).build(); // 412 Precondition Failed
            }

            // Proceed with update — JPA @Version will increment version on commit
            order.applyUpdate(req);
            orderRepo.save(order);

            String newEtag = "\"" + order.getVersion() + "\"";
            return ResponseEntity.ok().eTag(newEtag).body(OrderDto.from(order));
        }

When to use: use If-Match to avoid lost updates in concurrent clients. Servers can require conditional updates (HTTP 428).

3) Create-if-not-exists with If-None-Match: "*"
    Client attempts to create resource only if it does not exist.
        PUT /orders/123
        If-None-Match: "*"
        Content-Type: application/json
        { ... }


Server behavior:
    @PutMapping("/{id}")
    public ResponseEntity<?> putCreateIfNotExists(@PathVariable String id,
                                                  @RequestHeader(value="If-None-Match", required=false) String ifNoneMatch,
                                                  @RequestBody CreateOrderRequest req) {
        boolean exists = orderRepo.existsById(id);
        if (ifNoneMatch != null && ifNoneMatch.equals("*")) {
            if (exists) {
                // resource exists – per RFC, should return 412 Precondition Failed
                return ResponseEntity.status(412).build();
            }
            // else create
            Order o = orderRepo.save(new Order(...));
            return ResponseEntity.status(201).eTag("\"" + o.getVersion() + "\"").body(OrderDto.from(o));
        }
        // fallback behavior: maybe overwrite or return 409 conflict depending on API design
    }

4) Using body hash ETag (when no version available)
    When you don't have a version in DB, compute an ETag from content. Beware CPU/memory for large bodies.
        private String computeETag(String body) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(body.getBytes(StandardCharsets.UTF_8));
                String base64 = Base64.getEncoder().encodeToString(digest);
                return "\"" + base64 + "\""; // quotes required
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        @GetMapping("/report/{id}")
        public ResponseEntity<String> getReport(@PathVariable String id,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
            String body = reportService.renderReport(id); // expensive string
            String eTag = computeETag(body);
            ServletWebRequest webRequest = new ServletWebRequest(request, response);
            if (webRequest.checkNotModified(eTag)) {
                return ResponseEntity.status(304).eTag(eTag).build();
            }
            return ResponseEntity.ok().eTag(eTag).body(body);
        }

Tradeoffs: hashing large responses is costly (CPU & memory). Prefer DB version where possible.
    5) Enable automatic ETag for GET/HEAD responses (ShallowEtagHeaderFilter)
        Spring provides a filter that calculates ETag by buffering response. Useful for small responses.
        import org.springframework.boot.web.servlet.FilterRegistrationBean;
        import org.springframework.context.annotation.Bean;
        import org.springframework.web.filter.ShallowEtagHeaderFilter;
        @Bean
        public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagFilter() {
            FilterRegistrationBean<ShallowEtagHeaderFilter> fr = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
            fr.addUrlPatterns("/api/*"); // limit to API paths that are safe to buffer
            fr.setName("etagFilter");
            return fr;
        }


Warning: buffers the entire response — avoid for large/streaming responses.
    Interaction with Cache-Control and Proxies
    Cache-Control instructs how long caches may store responses (e.g., max-age=60, public).
    ETag/If-None-Match let caches revalidate after expiry: client or intermediary cache issues conditional request with If-None-Match to revalidate.
    Always set sensible Cache-Control for static assets (long max-age + versioned URLs) and more conservative caching for dynamic endpoints (or use validation with ETag).

Example flows (client/server interaction)
    Case A — Caching validation (GET)
        Server returns ETag: "v3" and Cache-Control: max-age=60.
        Client caches. After expiry, client issues:
            GET /orders/1
            If-None-Match: "v3"
        If content unchanged → server responds 304 Not Modified (no body) with same ETag.
        Client uses cached copy.

Case B — Safe update using If-Match (PUT)
    Client GETs resource, receives ETag: "2".
    Client modifies and issues:
    PUT /orders/1
    If-Match: "2"
    Server checks current ETag; if still "2" → apply update and return 200 with new ETag "3". If not → return 412 Precondition Failed.

Common pitfalls
    Forgetting quotes: Some clients/servers expect quoted ETag values. Use Spring’s ResponseEntity.eTag(...) which manages quoting.
    Computing ETag for streaming responses: not feasible without buffering.
    Weak ETags + updates: servers must not use weak ETags for concurrency control (If-Match requires strong ETag to guarantee byte equality).
    Relying solely on Last-Modified: If-Modified-Since uses second precision and can misclassify rapid updates.

Recommended pragmatic approach
    If you have a DB @Version (or incrementing sequence), use that for ETag — cheap and reliable for caching and optimistic locking.
    For large payloads that change frequently, set Cache-Control: no-cache and rely on conditional requests with ETag for validation.
    Use If-Match on writes to prevent lost updates, optionally return 428 Precondition Required to force clients to perform conditional
        updates if necessary.
    For static assets, use long Cache-Control and content-hash filenames (cache busting) so you rarely need ETags.

If you want, I can:
    generate a small runnable Spring Boot example repository (controller + entity + tests) that demonstrates:
    GET with ETag & Last-Modified
    PUT with If-Match concurrency control (returns 412 on mismatch)
    If-None-Match: "*" create-only semantics
or produce a one-page cheat-sheet PDF you can print for interview/reference.
