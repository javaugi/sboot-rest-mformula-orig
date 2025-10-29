/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.spring5.entity.AlgoTrade;
import com.spring5.repository.AlgoTradeRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author javaugi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlgoTradeService {

	private final AlgoTradeRepository algoTradeRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, AlgoTrade> redisTemplate;
    private final KafkaTemplate kafkaTemplate;
    private final @Qualifier("tradeServiceCircuitBreaker")
    CircuitBreaker circuitBreaker;
    private final @Qualifier("tradeServiceRateLimiter")
    RateLimiter rateLimiter;

    /*
	 * private final @Qualifier("tradeServiceRetry") Retry retry; //
     */
    private static final String TRADE_CACHE_PREFIX = "trade:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    private final Counter tradeCounter;
    private final Timer tradeTimer;
    private final Gauge tradeAmountGauge;

    AtomicDouble amount = new AtomicDouble(0);

    public AlgoTradeService(AlgoTradeRepository algoTradeRepository, RestTemplate restTemplate,
            RedisTemplate<String, AlgoTrade> redisTemplate, KafkaTemplate kafkaTemplate,
            @Qualifier("tradeServiceCircuitBreaker") CircuitBreaker circuitBreaker,
            @Qualifier("tradeServiceRateLimiter") RateLimiter rateLimiter, MeterRegistry registry) {
        this.algoTradeRepository = algoTradeRepository;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.circuitBreaker = circuitBreaker;
        this.rateLimiter = rateLimiter;

        this.tradeCounter = Counter.builder("trades.total")
                .description("Total number of trades")
                .tag("service", "trade")
                .register(registry);

        this.tradeTimer = Timer.builder("trades.duration")
                .description("Trade processing time")
                .register(registry);

        this.tradeAmountGauge = Gauge.builder("trades.amount", amount, AtomicDouble::get)
                .description("Current trade amount")
                .register(registry);
    }

    public void processTrade(AlgoTrade trade) {
        double amount = trade.getQuantity() * trade.getPrice();
        tradeTimer.record(() -> {
            // Business logic
            tradeCounter.increment();
            //tradeAmountGauge.set(amount);
        });
    }

    public AlgoTrade findById(Long id) {
		return algoTradeRepository.findById(id).orElse(new AlgoTrade());
	}

	public List<AlgoTrade> findByIds(List<Long> ids) {
		try {
			return algoTradeRepository.findByIds(ids);
		}
		catch (Exception ex) {
			return Collections.EMPTY_LIST;
		}
	}

	public List<AlgoTrade> findAll() {
		return algoTradeRepository.findAll();
	}

	public Iterable<AlgoTrade> findAll(Sort sort) {
		return algoTradeRepository.findAll();
	}

	public Page<AlgoTrade> findAll(Pageable pageable) {
		return algoTradeRepository.findAll(pageable);
	}

	@Transactional
	public AlgoTrade save(AlgoTrade trade) {
		return algoTradeRepository.save(trade);
	}

	@Transactional
	public List<AlgoTrade> saveAll(List<AlgoTrade> trades) {
		return algoTradeRepository.saveAll(trades);
	}

	@Transactional
	public AlgoTrade update(AlgoTrade trade) {
		return algoTradeRepository.save(trade);
	}

	public List<AlgoTrade> getByUserEmail(String userEmail) {
		try {
			return algoTradeRepository.findByUserEmail(userEmail);
		}
		catch (Exception ex) {
			return Collections.EMPTY_LIST;
		}
	}

	public Boolean addMoney(Long userAccountId, BigDecimal amount) {
		try {
			algoTradeRepository.addMoney(userAccountId, amount);
		}
		catch (Exception ex) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	public Boolean deleteById(Long id) {
		try {
			algoTradeRepository.deleteById(id);
		}
		catch (Exception ex) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/*
	 * Here are several strategies to handle the performance issues when making individual
	 * external API calls for hundreds of trade IDs:
	 * 
	 * 1. Batch Processing with Parallel Execution A. Using CompletableFuture for Parallel
	 * Calls
	 */
	public List<AlgoTrade> getTradeBatchCompletableFuture(List<Long> tradeIds) {
		List<CompletableFuture<AlgoTrade>> futures = tradeIds.stream()
                .map(tradeId -> CompletableFuture.supplyAsync(() -> getTradeExternal(tradeId), executorService))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
	}

    public AlgoTrade getTradeExternal(Long id) {
		try {
			/*
			 * String url = "https://api.external.com/trades/" + tradeId;
			 * ResponseEntity<AlgoTrade> response = restTemplate.getForEntity(url,
			 * AlgoTrade.class); return response.getBody(); //
			 */
			return this.findById(id);
		}
		catch (RestClientException e) {
			log.error("Error fetching id {}: {}", id, e.getMessage());
			return null;
		}
	}

	// B. Using Spring's @Async for Asynchronous Processing
	@Async("tradeTaskExecutor")
	public CompletableFuture<AlgoTrade> getTradeAsync(Long id) {
		return CompletableFuture.completedFuture(getTradeExternal(id));
	}

	public List<AlgoTrade> getTradesParallelBySpringAsync(List<Long> tradeIds) {
		List<CompletableFuture<AlgoTrade>> futures = tradeIds.stream()
			.parallel()
			.map(this::getTradeAsync)
			.collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
	}

	@Configuration
	@EnableAsync
	public class AsyncConfig {

		@Bean("tradeTaskExecutor")
		public Executor taskExecutor() {
			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(10);
			executor.setMaxPoolSize(20);
			executor.setQueueCapacity(100);
			executor.setThreadNamePrefix("TradeExecutor-");
			executor.initialize();
			return executor;
		}

	}

	/*
	 * 2. Implement Bulk API Endpoint (Recommended) A. Advocate for Bulk API from External
	 * Service
	 */
	public List<AlgoTrade> getTradesBulkIfExteralApiPermits(List<Long> tradeIds) {
		// Split into chunks to avoid too large requests
		List<List<Long>> chunks = Lists.partition(tradeIds, 50); // 50 IDs per request

		return chunks.stream().map(this::getTradesBulkChunk).flatMap(List::stream).collect(Collectors.toList());
	}

	private List<AlgoTrade> getTradesBulkChunk(List<Long> tradeIds) {
		try {
			/*
			 * BulkTradeRequest request = new BulkTradeRequest(tradeIds);
			 * ResponseEntity<BulkTradeResponse> response = restTemplate.postForEntity(
			 * "https://api.external.com/trades/bulk", request, BulkTradeResponse.class);
			 * 
			 * return response.getBody().getTrades(); //
			 */
			return this.findByIds(tradeIds);
		}
		catch (Exception e) {
			log.error("Error in bulk trade request: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	// DTOs for bulk API
	@Data
	public class BulkTradeRequest {

		private List<Long> tradeIds;

		public BulkTradeRequest(List<Long> tradeIds) {
			this.tradeIds = tradeIds;
		}

	}

	@Data
	public class BulkTradeResponse {

		private List<AlgoTrade> trades;

	}

	/*
	 * 3. Caching Strategy A. Implement Redis Cache
	 */
	public List<AlgoTrade> getTradesWithRedisCache(List<Long> tradeIds) {
		Map<Long, AlgoTrade> cachedTrades = getCachedTrades(tradeIds);
		List<Long> missingIds = getMissingTradeIds(tradeIds, cachedTrades);

		List<AlgoTrade> fetchedTrades = fetchMissingTrades(missingIds);
		cacheTrades(fetchedTrades);

		return combineResults(tradeIds, cachedTrades, fetchedTrades);
	}

	private List<Long> getMissingTradeIds(List<Long> tradeIds, Map<Long, AlgoTrade> cachedTrades) {
		List<Long> missingIds = new ArrayList<>();

		tradeIds.stream().forEach(id -> {
			if (!cachedTrades.keySet().contains(id)) {
				missingIds.add(id);
			}
		});
		return missingIds;
	}

	private List<AlgoTrade> combineResults(List<Long> tradeIds, Map<Long, AlgoTrade> cachedTrades,
			List<AlgoTrade> fetchedTrades) {
		List<AlgoTrade> trades = new ArrayList<>();

		tradeIds.stream().forEach(id -> {
			if (cachedTrades.keySet().contains(id)) {
				trades.add(cachedTrades.get(id));
			}
			else {
				fetchedTrades.stream().forEach(t -> {
					if (Objects.equals(t.getId(), id)) {
						trades.add(t);
					}
				});
			}
		});

		return trades;
	}

	private Map<Long, AlgoTrade> getCachedTrades(List<Long> tradeIds) {
		List<String> cacheKeys = tradeIds.stream().map(id -> TRADE_CACHE_PREFIX + id).collect(Collectors.toList());

		List<AlgoTrade> cached = redisTemplate.opsForValue().multiGet(cacheKeys);

		Map<Long, AlgoTrade> result = new HashMap<>();
		for (int i = 0; i < tradeIds.size(); i++) {
			if (cached.get(i) != null) {
				result.put(tradeIds.get(i), cached.get(i));
			}
		}
		return result;
	}

	private List<AlgoTrade> fetchMissingTrades(List<Long> missingIds) {
		if (missingIds.isEmpty()) {
			return Collections.emptyList();
		}

		getTradesBatchResilient(missingIds);
		getTradeBatchCompletableFuture(missingIds);
		getTradesParallelBySpringAsync(missingIds);
		getTradesBulkIfExteralApiPermits(missingIds); // if external service api permits
		return getTradesBatch(missingIds); // Use batch method from above
	}

	private List<AlgoTrade> getTradesBatch(List<Long> missingIds) {
		return this.findByIds(missingIds);
	}

	private void cacheTrades(List<AlgoTrade> trades) {
		trades.forEach(trade -> {
			String key = TRADE_CACHE_PREFIX + trade.getId();
			redisTemplate.opsForValue().set(key, trade, CACHE_TTL);
		});
	}

	/*
	 * 4. Rate Limiting and Circuit Breaker A. Using Resilience4j for Fault Tolerance
	 */
	// Batch processing with resilience
	public List<AlgoTrade> getTradesBatchResilient(List<Long> tradeIds) {
		List<CompletableFuture<AlgoTrade>> futures = tradeIds.stream()
			.map(tradeId -> CompletableFuture.supplyAsync(() -> getTrade(tradeId), executorService))
			.collect(Collectors.toList());

		return futures.stream().map(future -> {
			try {
				return future.get(10, TimeUnit.SECONDS);
			}
			catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.error("Error in future: {}", e.getMessage());
				return null;
			}
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
	}

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "externalService", fallbackMethod = "getTradeFallback")
    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "externalService")
    @io.github.resilience4j.retry.annotation.Retry(name = "externalService")
	public AlgoTrade getTrade(Long tradeId) {
		return rateLimiter.executeSupplier(() -> circuitBreaker.executeSupplier(() -> getTradeExternal(tradeId)));
	}

	public AlgoTrade getTradeFallback(Long tradeId, Exception e) {
		log.warn("Fallback for trade {}: {}", tradeId, e.getMessage());
		// Return cached version or default value
		return getCachedTrade(tradeId);
	}

	private AlgoTrade getCachedTrade(Long tradeId) {
		return redisTemplate.opsForValue().get(TRADE_CACHE_PREFIX + tradeId);
	}

	/*
	 * 5. Hybrid Approach with Queuing A. Using Message Queue for Background Processing
	 */
	public void processTradesKafkaAsync(List<Long> tradeIds) {
		tradeIds.forEach(tradeId -> {
			TradeRequest request = new TradeRequest(tradeId);
			kafkaTemplate.send("trade-requests", tradeId, request);
		});
	}

	@KafkaListener(topics = "trade-requests")
	public void processTradeRequest(TradeRequest request) {
		AlgoTrade trade = getTradeExternal(request.getTradeId());
		algoTradeRepository.save(trade);
	}

	/*
	 * public List<AlgoTrade> getProcessedTrades(List<Long> tradeIds) { return
	 * algoTradeRepository.findAllById(tradeIds); } //
	 */
	@Data
	@Builder(toBuilder = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TradeRequest {

		Long tradeId;

	}

	// 6. Complete Optimized Solution
	public List<AlgoTrade> getTradesOptimized(List<Long> tradeIds) {
		// Step 1: Try to get from cache
		Map<Long, AlgoTrade> cachedTrades = getCachedTrades(tradeIds);
		List<Long> missingIds = getMissingTradeIds(tradeIds, cachedTrades);

		if (missingIds.isEmpty()) {
			// return mapToOriginalOrder(tradeIds, cachedTrades);
		}

		// Step 2: Fetch missing trades in parallel with resilience
		List<AlgoTrade> fetchedTrades = fetchTradesWithResilience(missingIds);

		// Step 3: Cache the newly fetched trades
		cacheTrades(fetchedTrades);

		// Step 4: Combine results
		return combineResults(tradeIds, cachedTrades, fetchedTrades);
	}

	private List<AlgoTrade> fetchTradesWithResilience(List<Long> tradeIds) {
		List<CompletableFuture<AlgoTrade>> futures = tradeIds.stream()
			.map(id -> CompletableFuture.supplyAsync(() -> circuitBreaker.executeSupplier(() -> getTradeExternal(id)),
					executorService))
			.collect(Collectors.toList());

		return futures.stream().map(future -> {
			try {
				return future.get(5, java.util.concurrent.TimeUnit.SECONDS); // Timeout
																				// protection
			}
			catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.error("Error fetching trade: {}", e.getMessage());
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	// Optimized Version 2
	public List<AlgoTrade> getTradesOptimizedV2(List<Long> tradeIds) {
		List<CompletableFuture<AlgoTrade>> futures = tradeIds.stream()
			.map(tradeId -> CompletableFuture.supplyAsync(() -> getTradeResilient(tradeId), executorService))
			.collect(Collectors.toList());

		return futures.stream().map(this::getFutureResult).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private AlgoTrade getTradeResilient(Long tradeId) {
		// Create the supplier
		Supplier<AlgoTrade> tradeSupplier = () -> {
			log.debug("Fetching trade: {}", tradeId);
			return getTradeExternal(tradeId);
		};

		// Apply resilience patterns using Decorators
		Supplier<AlgoTrade> decoratedSupplier = Decorators.ofSupplier(tradeSupplier)
			.withCircuitBreaker(circuitBreaker)
			.withRateLimiter(rateLimiter)
			.withRetry(Retry.ofDefaults("algoTradeService")) // Use default retry
			.decorate();

		try {
			return decoratedSupplier.get();
		}
		catch (Exception e) {
			log.error("Failed to fetch trade {} after retries: {}", tradeId, e.getMessage());
			return createFallbackTrade(tradeId);
		}
	}

	private AlgoTrade getFutureResult(CompletableFuture<AlgoTrade> future) {
		try {
			return future.get(10, TimeUnit.SECONDS);
		}
		catch (java.util.concurrent.TimeoutException e) {
			log.error("Future timed out");
			future.cancel(true);
		}
		catch (InterruptedException | ExecutionException e) {
			log.error("Error getting future result: {}", e.getMessage());
		}
		return null;
	}

	private AlgoTrade createFallbackTrade(Long tradeId) {
		return AlgoTrade.builder().id(tradeId).status("UNAVAILABLE").build();
	}

	/*
	 * 7. Configuration and Monitoring yaml # application.yml external: api: base-url:
	 * https://api.external.com timeout: 5000 max-connections: 100 max-per-route: 20
	 * 
	 * resilience4j: circuitbreaker: instances: tradeService: registerHealthIndicator:
	 * true slidingWindowSize: 10 ratelimiter: instances: tradeService: limitForPeriod: 10
	 * limitRefreshPeriod: 1s timeoutDuration: 5s Performance Comparison Approach 100
	 * Trades 1000 Trades Pros Cons Sequential ~100s ~1000s Simple Very slow Parallel (20
	 * threads) ~5s ~50s Fast External API load Bulk API ~2s ~4s Very fast Requires API
	 * change Cached + Parallel ~1s ~2s Fastest Stale data possible Recommendation
	 * Priority: Try to get a bulk API endpoint (most efficient) Implement caching +
	 * parallel execution Add rate limiting and circuit breakers Use async processing with
	 * proper thread pooling
	 */
    public List<AlgoTrade> getTradesOptimizedV3(List<Long> tradeIds) {
        // Step 1: Try to get from cache
        Map<Long, AlgoTrade> cachedTrades = getCachedTradesV3(tradeIds);
        List<Long> missingIds = getMissingTradeIdsV3(tradeIds, cachedTrades);

        if (missingIds.isEmpty()) {
            return new ArrayList<>(cachedTrades.values());
        }

        List<AlgoTrade> remainings = getRemainingTrades(missingIds);

        List<AlgoTrade> mergedList = Stream
                .concat(remainings.stream(), cachedTrades.values().stream())
                .collect(Collectors.toList());

        return mergedList;
    }

    private List<AlgoTrade> getRemainingTrades(List<Long> missingIds) {
        List<CompletableFuture<AlgoTrade>> futures = missingIds.stream()
                .map(this::proecssTradeById)
                .collect(Collectors.toList());

        /*
        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        CompletableFuture<List<AlgoTrade>> allResults = allDone.thenApply(v -> {
            return futures.stream().map(CompletableFuture::join)
                    .filter(Objects::nonNull).collect(Collectors.toList());

        });
        List<AlgoTrade> remainings = getFinalResults(allResults);
        // */

        List<AlgoTrade> remainings = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return remainings;
    }

    private List<AlgoTrade> getFinalResults(CompletableFuture<List<AlgoTrade>> allResults) {
        try {
            return allResults.get();
        } catch (InterruptedException | ExecutionException ex) {

        }
        return Collections.emptyList();
        //return Collections.EMPTY_LIST;  // this is untyped list predating java 1.5
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "externalService",
            fallbackMethod = "getTradeFallbackAudit")
    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "externalService")
    @io.github.resilience4j.retry.annotation.Retry(name = "externalService")
    public AlgoTrade getTradeResilientById(Long id) {
        try {
            /*
			 * String url = "https://api.external.com/trades/" + tradeId;
			 * ResponseEntity<AlgoTrade> response = restTemplate.getForEntity(url,
			 * AlgoTrade.class); return response.getBody(); //
             */
            return this.findById(id);
        } catch (RestClientException e) {
            log.error("Error fetching id {}: {}", id, e.getMessage());
            return null;
        }
    }

    private final CopyOnWriteArrayList<Long> REMAININGS = new CopyOnWriteArrayList();
    public AlgoTrade getTradeFallbackAudit(Long tradeId, Exception e) {
        log.warn("Fallback for trade {}: {}", tradeId, e.getMessage());
        // Return cached version or default value
        // add logging
        REMAININGS.add(tradeId);
        return getCachedTrade(tradeId);
    }


    private CompletableFuture<AlgoTrade> proecssTradeById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            return getTradeResilientById(id);
        }, executorService);
    }


    private List<Long> getMissingTradeIdsV3(List<Long> tradeIds, Map<Long, AlgoTrade> cachedTrades) {
        List<Long> missingIds = new ArrayList<>();

        tradeIds.stream().forEach(id -> {
            if (!cachedTrades.keySet().contains(id)) {
                missingIds.add(id);
            }
        });
        return missingIds;
    }

    private Map<Long, AlgoTrade> getCachedTradesV3(List<Long> tradeIds) {
        List<String> cacheKeys = tradeIds.stream().map(id -> TRADE_CACHE_PREFIX + id).collect(Collectors.toList());

        List<AlgoTrade> cached = redisTemplate.opsForValue().multiGet(cacheKeys);

        Map<Long, AlgoTrade> result = new HashMap<>();
        for (int i = 0; i < tradeIds.size(); i++) {
            if (cached.get(i) != null) {
                result.put(tradeIds.get(i), cached.get(i));
            }
        }
        return result;
    }

}
