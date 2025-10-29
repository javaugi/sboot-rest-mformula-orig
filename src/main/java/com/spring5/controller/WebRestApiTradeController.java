/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

//import static com.mongodb.client.model.Filters.where;
import com.spring5.entity.AlgoTrade;
import com.spring5.mongodb.Customer;
import com.spring5.mongodb.dto.PagedCustomers;
import com.spring5.service.AlgoTradeR2dbcService;
import com.spring5.service.AlgoTradeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import static org.springframework.data.relational.core.query.Criteria.where;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
4. Endpoints Summary
HTTP Method     URL                         Description

POST            /api/products               Create product (MVC)
PUT             /api/products/{id}          Replace product (MVC)
PATCH           /api/products/{id}          Partial update (MVC)

POST            /reactive/products          Create product (Reactive)
PUT             /reactive/products/{id}     Replace product (Reactive)
PATCH           /reactive/products/{id}     Partial update (Reactive)

1. Key Differences: @PostMapping vs @PutMapping
Aspect              @PostMapping (POST)                                         @PutMapping (PUT)
Intent              Create a new resource (server generates the ID).            Create or replace a resource at a specific ID/URI.
Idempotency         Not idempotent: Multiple calls → multiple resources.        Idempotent: Multiple calls → same final resource state.
Request URL         /api/users → server assigns ID.                             /api/users/{id} → client specifies ID.
HTTP Semantics      POST /users means "add to this collection."                 PUT /users/123 means "store this resource at /users/123."
Response            201 Created + location header with new ID.                  200 OK or 204 No Content if updated successfully.
Partial updates     Typically not used for partial updates.                     Typically not used for partial updates (use PATCH).

4. Idempotency in Practice
Operation               POST (/users)                       PUT (/users/123)
First request           Creates User ID 123 automatically	Creates or replaces ID 123
Second request (same)	Creates another new user            Just replaces ID 123 → same result

This makes PUT safe for retries in distributed systems (e.g., network failures).

5. When to Use Which
Use POST:
    Creating new resource without a known ID.
    Submitting commands or actions that aren’t simple updates (e.g., "process payment").
    Non-idempotent operations where duplication might occur.

Use PUT:
    Full replacement of a known resource.
    Idempotent updates — clients can retry safely.
    Upsert semantics: create if doesn’t exist, replace if exists.

For partial updates, use:
    @PatchMapping("/{id}")
    with JSON Merge Patch or JSON Patch.

6. Quick Cheat Sheet
    GET  = Retrieval                        200 OK or 404 Not Found
    POST = Create                           201 Created or 400 Bad Request
    PUT = Create or Replace (idempotent)    201 Created or 400 Bad Request
    PATCH = Partial Update
    Delete = Delete the resource            204 No Content or 400 Bad Request
 */
@Slf4j
@AllArgsConstructor
// @RestController
@RequestMapping("/web/apirest")
public class WebRestApiTradeController {

	private final AlgoTradeR2dbcService algoTradeR2dbcService;

	private final AlgoTradeService algoTradeService;

	private final RestTemplate restTemplate;

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    //private final ReactiveMongoTemplate template;
    private final MeterRegistry meterRegistry;

    private static final String ETAG_MODIFIED_MISMATCH = "{\"error\": \"ETag mismatch. Resource was modified by another request.\"}";

	/*
	 * Type 1: API Implementation Example Question:
	 * "Design an endpoint that returns a user's recent transactions with filtering capabilities."
	 */
	/*
	  @GetMapping("/users/{userId}/transactions") public Page<AlgoTrade> getTransactions(	  
	  @PathVariable String userId,	                       // {} used for userid
	  @RequestParam(required = false) String transactions,	  no {} for transactions
	  @RequestParam(required = false) LocalDate startDate,	  
	  @RequestParam(required = false) LocalDate endDate,	  
	  @RequestParam(required = false) TransactionType type,	  
	  @PageableDefault Pageable pageable) {
	  
	  // Discussion points: // - Pagination implementation // - Filter validation // -
	  Performance considerations (indexing) // - Error handling } //
   	 */
	/*
	 * 3. Summary Table Header Usage Example Value Cache-Control Cache policy (e.g.,
	  max-age, no-cache) max-age=600, public Expires When resource becomes stale Wed, 21
	  Oct 2025 07:28:00 GMT ETag Version identifier for resource "v1.0" Last-Modified
	  Last time resource changed Wed, 21 Oct 2025 07:28:00 GMT
	  
	  
	  ✅ Key Differences 
      Table Feature         ETag / Last-Modified   Idempotency Key     Purpose 
        Cache validation, optimistic concurrency Prevent duplicate side-effects Scope
	  Per-resource state Per request/operation Client supplies? Not required (server
	  sends ETag, client echoes) Yes, client must send unique key Used with GET (304),
	  PUT/DELETE (412) POST/PUT/DELETE (safe retries) Server logic Compare
	  version/timestamp with current state Store key+response, reuse if retried Failure
	  status 304 Not Modified, 412 Precondition Failed 409 Conflict or same 200 result on
	  retry Typical lifetime Until resource changes TTL window (e.g., 24h)
	  
	  1) Simple GET with ETag (computed from DB version) + Last-Modified 2) Conditional
	  Update (PUT) using If-Match for optimistic locking
   	 */
	/*
	  1. Query Parameters ($\texttt{?key=value&key2=value2}$)
	  
	  @RequestParam String username, String ifMatch = request.getParameter("username");
	  curl http://localhost:8088/web/apirest/cache?username=MyUsername 
    2. Request
	  Headers
	  
	  @GetMapping public ResponseEntity<List<AlgoTrade>>
	  getAllWithCacheControl(@PathVariable String username,
	  
	  @RequestParam String username, @RequestHeader("Authorization") String authToken) {
	  }
	  
	  @RequestHeader("Authorization") String authToken,
	  request.getHeader("Authorization"); 3. Request Body (Payload) for requests like
	  POST or PUT, the primary data is sent in the body of the HTTP request, which is not
	  part of the URL path.
	  
	  @RequestBody User newUser
	  
	  @PutMapping("/{username}") public ResponseEntity<AlgoTrade> update(@RequestBody
	  AlgoTrade newTrade) { } 4. Path Variables
	  
	  @GetMapping("/{username}") public ResponseEntity<List<AlgoTrade>>
	  getAllWithCacheControl(@PathVariable String username) { } curl
	  http://localhost:8088//web/apirest/cache/MyUsername 5. Cookies
	  
	  @GetMapping public ResponseEntity<List<AlgoTrade>>
	  getAllWithCacheControl(@CookieValue("username") String username) { }
	  
	  @CookieValue("sessionId") String username 6. Matrix Variables: These allow passing
	  parameters as part of the URI path, separated by a semicolon (;)
	  
	  @GetMapping("/resources/{id;version}") public ResponseEntity<List<AlgoTrade>>
	  getAllWithCacheControl(@MatrixVariable String id, @MatrixVariable String version) {
	  }
	  
   	 */
    @Cacheable(value = "trades", key = "#page + '-' + #size")
    public Page<AlgoTrade> getAllTrades(int page, int size) {
        return algoTradeService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/cache/{pathusername}")
    public ResponseEntity<List<AlgoTrade>> getAllWithCacheControl(@PathVariable("pathusername") String pathusername,
            @RequestParam("username") String username, @RequestHeader("Authorization") String authToken,
            @CookieValue("usernameCookie") String usernameCookie, HttpServletRequest request) {
		// jakarta.servlet.http.HttpServletRequest;
		// org.springframework.web.bind.annotation.PathVariable
		// org.springframework.web.bind.annotation.RequestParam;
		// org.springframework.web.bind.annotation.RequestHeader
		// org.springframework.web.bind.annotation.CookieValue
		String ifMatch = request.getParameter("If-Match");
		String idempotencyKey = request.getHeader("Idempotency-Key");

		return ResponseEntity.ok()
			.cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES).cachePublic())
			// .expires(Instant.now().plusSeconds(600).toEpochMilli())
			.body(algoTradeService.findAll());
		/*
		 * Cache-Control: public, max-age=600 Expires: <600 seconds from now>
		 */
	}

	// 1) Simple GET with ETag (computed from DB version) + Last-Modified
	@GetMapping("/etag/{id}")
	public ResponseEntity<AlgoTrade> getAlgoTradeById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) {
		AlgoTrade trade = algoTradeService.findById(id);
		if (trade.getId() == null) {
			return ResponseEntity.notFound().build();
		}

		// 1) Build a cheap ETag from DB version
		String eTag = calculateETag(trade);
		// 2) Last-Modified from updatedAt
		long lastModified = trade.getUpdateDate().toEpochMilli();
		// 3) Let Spring helper check If-None-Match / If-Modified-Since
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		// org.springframework.web.context.request.ServletWebRequest;
		// checkNotModified returns true if the request is conditional and NOT modified
		if (webRequest.checkNotModified(eTag, lastModified)) {
			// ServletWebRequest already set response status to 304
			// ServletWebRequest.checkNotModified(eTag, lastModified) inspects
			// If-None-Match and
			// If-Modified-Since and returns true if not modified.
			return ResponseEntity.status(304).eTag(eTag).lastModified(lastModified).build();
		}

		String ifMatch = request.getHeader("If-Match");
		String ifNoneMatch = request.getHeader("If-None-Match");
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if (eTag.equals(ifMatch) && !eTag.equals(ifNoneMatch) && (ifModifiedSince < lastModified)) {
			return ResponseEntity.status(304).eTag(eTag).lastModified(lastModified).build();
		}

		// 4) Return full resource with caching headers
		return ResponseEntity.ok()
			.eTag(eTag)
			.lastModified(lastModified)
			.cacheControl(CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS).cachePublic())
			.body(trade);
	}

	// 2) Conditional Update (PUT) using If-Match for optimistic locking
	// 3) Create-if-not-exists with If-None-Match: "*"
	@PutMapping("/etag/{id}")
	public ResponseEntity<?> updateAlgoTradeById(@PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response, @RequestBody AlgoTrade updatedTrade) {

		String ifMatch = request.getHeader("If-Match");
		String ifNoneMatch = request.getHeader("If-None-Match");
		AlgoTrade existingTrade = algoTradeService.findById(id);

		if (existingTrade.getId() == null) {
			// 3) Create-if-not-exists with If-None-Match: "*"
			if (ifNoneMatch != null && ifNoneMatch.equals("*")) {
				AlgoTrade savedTrage = algoTradeService.save(updatedTrade);
				String newETag = calculateETag(savedTrage);
				return ResponseEntity.status(201)
					.eTag(newETag)
					.lastModified(savedTrage.getUpdateDate().toEpochMilli())
					.cacheControl(CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS).cachePublic())
					.body(savedTrage);
			}
		}

		// 1) Build a cheap ETag from DB version
		String eTag = calculateETag(existingTrade);
		// 2) Last-Modified from updatedAt
		long lastModified = existingTrade.getUpdateDate().toEpochMilli();

		if (ifMatch == null) {
			// You may reject non-conditional updates with 428 or proceed depending on
			// policy
			// return ResponseEntity.status(428).body("Precondition required (send
			// If-Match)");
		}
		// If-Match can be a list of ETags. Simple compare:
		if (ifMatch != null && !ifMatch.equals(eTag) && !ifMatch.equals("*")) {
			// Precondition failed — resource changed since client last saw it
			return ResponseEntity.status(412).build(); // 412 Precondition Failed
		}

		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		// checkNotModified returns true if the request is conditional and NOT modified
		if (!webRequest.checkNotModified(eTag, lastModified)) {
			// ServletWebRequest already set response status to 304
			// ServletWebRequest.checkNotModified(eTag, lastModified) inspects
			// If-None-Match and
			// If-Modified-Since and returns true if not modified.
			return ResponseEntity.status(412).eTag(eTag).lastModified(lastModified).build();
		}

		existingTrade.setPrice(updatedTrade.getPrice());
		if (updatedTrade.getVersion() != null && updatedTrade.getVersion() > existingTrade.getVersion()) {
			existingTrade.setVersion(updatedTrade.getVersion());
		}
		AlgoTrade savedTrage = algoTradeService.save(existingTrade);
		String newETag = calculateETag(savedTrage);

		return ResponseEntity.ok()
			.eTag(newETag)
			.lastModified(lastModified)
			.cacheControl(CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS).cachePublic())
			.body(savedTrage);
	}

	private String calculateETag(AlgoTrade trade) {
		// Create a hash of the important fields that determine consistency
		String input = trade.getId() + ":" + trade.getVersion() + ":" + trade.getUpdateDate();
		return "\"" + DigestUtils.md5DigestAsHex(input.getBytes()) + "\"";
	}

	public static String computeETag(String body) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(body.getBytes(StandardCharsets.UTF_8));
			String base64 = Base64.getEncoder().encodeToString(digest);
			return "\"" + base64 + "\""; // quotes required
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	// 2) ETag on GET + Idempotency on POST - this works like PUT
	@PostMapping("/etag/create")
	public ResponseEntity<AlgoTrade> addTrade(@RequestBody AlgoTrade trade,
			@RequestHeader("Idempotency-Key") String key) {
		AlgoTrade existingTrade = null;
		if (trade.getId() != null) {
			existingTrade = algoTradeService.findById(trade.getId());
		}

		if (existingTrade != null && existingTrade.getId() != null) {
			return ResponseEntity.status(HttpStatus.FOUND).eTag(calculateETag(existingTrade)).body(existingTrade);
		}

		AlgoTrade createdTrade = algoTradeService.save(trade);
		return ResponseEntity.status(201)
			.eTag(calculateETag(existingTrade))
			.lastModified(createdTrade.getUpdateDate().toEpochMilli())
			.cacheControl(CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS).cachePublic())
			.body(createdTrade);
	}

	/*
	 * The classic performance bottleneck when a service processes hundreds (or thousands)
	 * of IDs sequentially and calls a slow external API one When each call is
	 * synchronous, you suffer because: 1. Network I/O waits dominate time. 2. No
	 * parallelism → total time = sum of all calls. 3. External API latency magnifies the
	 * delay. Recommended Solution 1. If immediate response is needed → Use Java's
	 * CompletableFuture, or Spring’s @Async, Stream Paralell or WebFlux or WebClient with
	 * concurrency control - Limit concurrency to avoid flooding the external service
	 * (e.g., 10–20 parallel calls). 2. If delayed processing is acceptable → Use Kafka
	 * for async processing with proper Resilience4j rate limiting, retries, backoff, and
	 * circuit breaker to avoid overwhelming the external service.. 3. Add caching,
	 * batching (if possible), and circuit breaker for resilience. Cache results for
	 * frequently requested IDs. Use @Cacheable("trades") in Spring Boot to avoid
	 * duplicate API calls.
	 */
	/*
	 * Here are several strategies to handle the performance issues when making individual
	 * external API calls for hundreds of trade IDs: 1. Batch Processing with Parallel
	 * Execution A. Using CompletableFuture for Parallel Calls
	 */
	public List<AlgoTrade> getTradeBatchCompletableFuture(List<Long> tradeIds) {
		return this.algoTradeService.getTradeBatchCompletableFuture(tradeIds);
	}

	// B. Using Spring's @Async for Asynchronous Processing
	public List<AlgoTrade> getTradesParallelBySpringAsync(List<Long> ids) {
		return this.algoTradeService.getTradesParallelBySpringAsync(ids);
	}

	/*
	 * 2. Implement Bulk API Endpoint (Recommended) if external service permits A.
	 * Advocate for Bulk API from External Service
	 */
	public List<AlgoTrade> getTradesBulkIfExteralApiPermits(List<Long> tradeIds) {
		return this.algoTradeService.getTradesBulkIfExteralApiPermits(tradeIds);
	}

	/*
	 * 3. Caching Strategy A. Implement Redis Cache
	 */
	public List<AlgoTrade> getTradesWithRedisCache(List<Long> tradeIds) {
		return this.algoTradeService.getTradesWithRedisCache(tradeIds);
	}

	/*
	 * 4. Rate Limiting and Circuit Breaker A. Using Resilience4j for Fault Tolerance
	 */
	public List<AlgoTrade> getTradesBatchResilient(List<Long> tradeIds) {
		return this.algoTradeService.getTradesBatchResilient(tradeIds);
	}

	/*
	 * 5. Hybrid Approach with Queuing A. Using Message Queue for Background Processing
	 */
	public List<AlgoTrade> processTradesKafkaAsync(List<Long> tradeIds) {
		this.algoTradeService.processTradesKafkaAsync(tradeIds);
		return this.algoTradeService.findByIds(tradeIds);
	}

	// 6. Complete Optimized Solution
	public List<AlgoTrade> getTradesOptimized(List<Long> tradeIds) {
		return this.algoTradeService.getTradesOptimized(tradeIds);
	}

	// Optimized Version 2
	public List<AlgoTrade> getTradesOptimizedV2(List<Long> tradeIds) {
		return this.algoTradeService.getTradesOptimizedV2(tradeIds);
	}

    // Optimized Version 2
    public List<AlgoTrade> getTradesOptimizedV3(List<Long> tradeIds) {
        return this.algoTradeService.getTradesOptimizedV2(tradeIds);
    }

    /*
	 * Performance Comparison Approach 100 Trades 1000 Trades Pros Cons Sequential ~100s
	 * ~1000s Simple Very slow Parallel (20 threads) ~5s ~50s Fast External API load Bulk
	 * API ~2s ~4s Very fast Requires API change Cached + Parallel ~1s ~2s Fastest Stale
	 * data possible
	 * 
	 * Recommendation Priority: Try to get a bulk API endpoint (most efficient) Implement
	 * caching + parallel execution Add rate limiting and circuit breakers Use async
	 * processing with proper thread pooling
	 */
	// the above are the strategies to handle the performance issues when making
	// individual external
	// API calls for hundreds of trade IDs:
	public Flux<AlgoTrade> getTradesByReactive(List<Long> tradeIds) {
		return Flux.fromIterable(tradeIds)
			// flatMap performs the concurrent, non-blocking API calls
			.flatMap(algoTradeR2dbcService::findById);
	}

    @CircuitBreaker(name = "externalApiCB", fallbackMethod = "fallbackTrade")
	public String callExternalApi(Long tradeId) {
		return restTemplate.getForObject("https://external-api/trades/" + tradeId, String.class);
	}

	public String fallbackTrade(Long tradeId, Throwable t) {
		return "Fallback for " + tradeId;
	}

	@GetMapping
	public ResponseEntity<List<AlgoTrade>> getAll() {
		return ResponseEntity.ok(algoTradeService.findAll());
	}

	@GetMapping("/react")
	public Flux<ResponseEntity<AlgoTrade>> getAllReact() {
        return algoTradeR2dbcService.findAll()
                .map(ResponseEntity::ok)
             			.defaultIfEmpty(ResponseEntity.notFound().build())
			.onErrorResume(Exception.class, ex -> {
				return Flux.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
			});
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AlgoTrade> getById(@PathVariable("id") Long id) {
		log.info("getUserById id {}", id);
		AlgoTrade existingTrade = algoTradeService.findById(id);
		if (existingTrade.getId() == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(existingTrade);
	}

	@GetMapping(value = "/react/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<AlgoTrade>> getByIdReact(@PathVariable("id") Long id) {
		log.info("getUserById id {}", id);
		return algoTradeR2dbcService.findById(id)
			.map(ResponseEntity::ok)
			.defaultIfEmpty(ResponseEntity.notFound().build())
			.onErrorResume(Exception.class, ex -> {
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
			});
	}

	@GetMapping("/{email}")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AlgoTrade>> getByUserEmail(@PathVariable String email) {
        List<AlgoTrade> trades = algoTradeService.getByUserEmail(email);
		if (trades == null || trades.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(trades);
	}

	@GetMapping(value = "/react/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ResponseEntity<AlgoTrade>> getByUserEmailReact(@PathVariable("email") String email) {
        return algoTradeR2dbcService.getByUserEmail(email)
             			.map(ResponseEntity::ok)
			.defaultIfEmpty(ResponseEntity.notFound().build())
			.onErrorResume(Exception.class, ex -> {
				return Flux.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
			});
	}

	@PostMapping("/create")
	public ResponseEntity<AlgoTrade> createTrade(@RequestBody AlgoTrade trade) {
		AlgoTrade createdTrade = algoTradeService.save(trade);
		return new ResponseEntity<>(createdTrade, HttpStatus.CREATED);
	}

	@PostMapping(value = "/react/create", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<AlgoTrade>> createReact(@RequestBody AlgoTrade trade) {
		return algoTradeR2dbcService.save(trade)
			.map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
			.switchIfEmpty(Mono.just(AlgoTrade.builder().build())
				.doOnNext(dummy -> log.info("Error create dummy {}", dummy))
				.then(Mono.empty()))
			.onErrorResume(e -> {
				log.error("Error create: " + e.getMessage());
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
			});
	}

	@PutMapping("/update-trade")
	public ResponseEntity<AlgoTrade> update(RequestEntity<AlgoTrade> request) {
		AlgoTrade trade = algoTradeService.update(request.getBody());
		if (trade != null) {
			return ResponseEntity.ok(trade);
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/react/{id}")
	public Mono<ResponseEntity<AlgoTrade>> updateReact(@PathVariable Long id, @Valid @RequestBody AlgoTrade trade,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		log.info("Received PUT request for id: {} with data: {}, Authorization header: {}", id, trade, authHeader);

		return algoTradeR2dbcService.findById(id).flatMap(existing -> {
			return algoTradeR2dbcService.save(existing);
		}).map(updatedUser -> {
			log.info("Successfully updateUser: {}", updatedUser);
			return ResponseEntity.ok(updatedUser);
		})
			.switchIfEmpty(Mono.just(AlgoTrade.builder().build())
				.doOnNext(dummy -> log.info("Error update dummy {}", dummy))
				.then(Mono.empty()))
			.doOnError(e -> log.error("Error update {}", e.getMessage()));
	}

	@DeleteMapping("/trade/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteByTradeId(@PathVariable Long id) {
		AlgoTrade trade = algoTradeService.findById(id);
		// Check if the trade was found
		if (trade.getId() == null) {
			// If not found, throw a ResponseStatusException with NOT_FOUND status
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trade not found with ID: " + id);
		}

		// If the trade is found, proceed with deletion
		boolean deleted = algoTradeService.deleteById(id);
		if (!deleted) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trade delete failed with ID: " + id);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		AlgoTrade trade = algoTradeService.findById(id);

		if (trade.getId() != null) {
			algoTradeService.deleteById(id); // Use deleteById for deleting by ID
			ResponseEntity.noContent().build();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for
																// successful deletion
		}
		else {
			ResponseEntity.notFound().build();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the
																// product doesn't exist
		}
	}

	@DeleteMapping("/react/{id}")
	public Mono<ResponseEntity<Void>> deleteByIdReact(@PathVariable("id") Long id) {
		return algoTradeR2dbcService.deleteById(id)
			.then(Mono.just(ResponseEntity.noContent().build()))
			.onErrorResume(e -> {
				log.error("Error create: " + e.getMessage());
				return Mono
					.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AlgoTrade.builder().build()));
			})
			.then(Mono.empty());
    }

    /*
    6. Why “Keyset” Pagination?
        Keyset pagination is preferred over OFFSET/LIMIT because:
            It’s faster for large datasets.
            It avoids skipping over many rows.
            It ensures stable ordering even as rows are added/removed.    
    
    Term                Meaning
    template            Reactive DB access helper (like R2dbcEntityTemplate or ReactiveMongoTemplate)
    query               A Spring Data Query defining filter (where), sort, and limit
    getTradesKeyset()	Returns a paged result (like “next page after lastId”) in a non-blocking, reactive fashion    
     */
    @Timed(value = "customer.get.trades.keyset", description = "Time taken to get paginated customers using keyset")
    public Mono<PagedCustomers> getUsersPageKeyset(Long lastId, int size) {
        Query query = Query.query(where("id").greaterThan(lastId == null ? 0 : lastId))
                .limit(size)
                .sort(Sort.by("id"));

        Mono<List<Customer>> trades = r2dbcEntityTemplate.select(query, Customer.class).collectList();
        Mono<Long> total = r2dbcEntityTemplate.select(Customer.class).count();
        return Mono.zip(trades, total)
                .map(t -> new PagedCustomers(t.getT1(), t.getT2()));
    }

    @Timed(value = "customer.get.users.page", description = "Time taken to get paginated customers")
    public Mono<PagedCustomers> getUsersPage(int page, int size) {
        int pageIndex = Math.max(0, page - 1);
        Query query = Query.empty()
                .limit(size)
                .offset((long) pageIndex * size)
                .sort(Sort.by("id"));

        Mono<List<Customer>> customersMono = r2dbcEntityTemplate.select(query, Customer.class).collectList();
        Mono<Long> totalMono = r2dbcEntityTemplate.select(Customer.class).count(); // total number of users
        return Mono.zip(customersMono, totalMono)
                .map(tuple -> new PagedCustomers(tuple.getT1(), tuple.getT2()));
    }

    public Mono<PagedCustomers> getUsersPageTimed(int page, int size) {
        Timer.Sample sample = Timer.start(meterRegistry);

        int pageIndex = Math.max(0, page - 1);
        Query query = Query.empty()
                .limit(size)
                .offset((long) pageIndex * size)
                .sort(Sort.by("id"));

        Mono<List<Customer>> customersMono = r2dbcEntityTemplate.select(query, Customer.class).collectList();
        Mono<Long> totalMono = r2dbcEntityTemplate.select(Customer.class).count();

        return Mono.zip(customersMono, totalMono)
                .map(tuple -> new PagedCustomers(tuple.getT1(), tuple.getT2()))
                .doOnSuccess(result -> {
                    sample.stop(meterRegistry.timer("customer.get.users.page",
                            "status", "success",
                            "page", String.valueOf(page),
                            "size", String.valueOf(size)));
                })
                .doOnError(error -> {
                    sample.stop(meterRegistry.timer("customer.get.users.page",
                            "status", "error",
                            "page", String.valueOf(page),
                            "size", String.valueOf(size)));
                });
    }

}

/*
 * /* Key Features
 * 
 * RESTful API with HATEOAS: Resources include links to related resources Follows REST
 * principles Self-descriptive messages
 * 
 * Web Interface with Spring MVC: Traditional server-side rendering Thymeleaf templates
 * for HTML generation Simple CRUD operations through web forms
 * 
 * Data Model: JPA entities with proper relationships Repository pattern for data access
 * 
 * Separation of Concerns: API endpoints separate from web interface Clear distinction
 * between data model and resource representation
 * 
 * This implementation provides a solid foundation that can be extended with additional
 * features like:
 * 
 * Authentication and authorization Validation Advanced search capabilities Pagination
 * Caching API documentation with Swagger
 */
