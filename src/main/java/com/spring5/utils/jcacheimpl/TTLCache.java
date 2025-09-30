/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.jcacheimpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Thread-safe cache with TTL (Time-To-Live) support Features: - Automatic
 * eviction of expired entries - LRU (Least Recently Used) eviction when
 * capacity is reached - Statistics tracking - Optional value loader for
 * cache-through pattern
 */
public class TTLCache<K, V> {

    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final ConcurrentLinkedDeque<K> accessQueue; // For LRU tracking
    private final ScheduledExecutorService cleanupExecutor;

    private final long defaultTTLMillis;
    private final int maxCapacity;
    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    private final AtomicLong evictionCount = new AtomicLong(0);

    public TTLCache(long defaultTTL, TimeUnit timeUnit, int maxCapacity) {
        this.defaultTTLMillis = timeUnit.toMillis(defaultTTL);
        this.maxCapacity = maxCapacity;
        this.cache = new ConcurrentHashMap<>(maxCapacity);
        this.accessQueue = new ConcurrentLinkedDeque<>();

        // Initialize cleanup scheduler
        this.cleanupExecutor
                = Executors.newSingleThreadScheduledExecutor(
                        r -> {
                            Thread t = new Thread(r, "TTLCache-Cleanup");
                            t.setDaemon(true);
                            return t;
                        });

        // Schedule periodic cleanup
        long cleanupInterval = Math.max(1000, defaultTTLMillis / 10); // Cleanup at least every second
        cleanupExecutor.scheduleAtFixedRate(
                this::cleanupExpiredEntries, cleanupInterval, cleanupInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Inner class representing a cache entry with expiration tracking
     */
    private static class CacheEntry<V> {

        private final V value;
        private final long expirationTime;
        private volatile long lastAccessTime;

        public CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public V getValue() {
            lastAccessTime = System.currentTimeMillis();
            return value;
        }
    }

    /**
     * Put a value in the cache with default TTL
     */
    public void put(K key, V value) {
        put(key, value, defaultTTLMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Put a value in the cache with custom TTL
     */
    public void put(K key, V value, long ttl, TimeUnit timeUnit) {
        if (key == null || value == null) {
            throw new NullPointerException("Key and value cannot be null");
        }

        long ttlMillis = timeUnit.toMillis(ttl);
        CacheEntry<V> entry = new CacheEntry<>(value, ttlMillis);

        cache.compute(
                key,
                (k, existingEntry) -> {
                    if (existingEntry != null) {
                        accessQueue.remove(k); // Remove from queue for re-insertion at end
                    }
                    return entry;
                });

        accessQueue.add(key); // Add to end of queue (most recently used)

        // Enforce capacity limit using LRU eviction
        enforceCapacity();
    }

    /**
     * Get a value from the cache, returns null if not found or expired
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);

        if (entry == null) {
            missCount.incrementAndGet();
            return null;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            accessQueue.remove(key);
            missCount.incrementAndGet();
            evictionCount.incrementAndGet();
            return null;
        }

        hitCount.incrementAndGet();
        accessQueue.remove(key); // Remove and re-add to mark as recently used
        accessQueue.add(key);
        return entry.getValue();
    }

    /**
     * Get a value from cache, computing it if not present (cache-through
     * pattern)
     */
    public V get(K key, Supplier<V> valueLoader) {
        return get(key, valueLoader, defaultTTLMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Get a value from cache with custom TTL, computing it if not present
     */
    public V get(K key, Supplier<V> valueLoader, long ttl, TimeUnit timeUnit) {
        V value = get(key);
        if (value == null) {
            value = valueLoader.get();
            if (value != null) {
                put(key, value, ttl, timeUnit);
            }
        }
        return value;
    }

    /**
     * Remove a specific key from the cache
     */
    public boolean remove(K key) {
        boolean removed = cache.remove(key) != null;
        if (removed) {
            accessQueue.remove(key);
        }
        return removed;
    }

    /**
     * Check if key exists in cache (may still be expired)
     */
    public boolean containsKey(K key) {
        CacheEntry<V> entry = cache.get(key);
        return entry != null && !entry.isExpired();
    }

    /**
     * Get the number of entries in the cache (including expired ones not yet
     * cleaned up)
     */
    public int size() {
        return cache.size();
    }

    /**
     * Get the number of valid (non-expired) entries
     */
    public int validSize() {
        return (int) cache.entrySet().stream().filter(entry -> !entry.getValue().isExpired()).count();
    }

    /**
     * Clear all entries from the cache
     */
    public void clear() {
        cache.clear();
        accessQueue.clear();
    }

    /**
     * Shutdown the cache and its cleanup thread
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Clean up expired entries and enforce LRU eviction if needed
     */
    private void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();

        // Remove expired entries
        cache
                .entrySet()
                .removeIf(
                        entry -> {
                            if (entry.getValue().isExpired()) {
                                accessQueue.remove(entry.getKey());
                                evictionCount.incrementAndGet();
                                return true;
                            }
                            return false;
                        });

        // Enforce capacity after cleanup
        enforceCapacity();
    }

    /**
     * Enforce maximum capacity using LRU eviction
     */
    private void enforceCapacity() {
        while (cache.size() > maxCapacity && !accessQueue.isEmpty()) {
            K oldestKey = accessQueue.poll(); // Remove from front (least recently used)
            if (oldestKey != null) {
                if (cache.remove(oldestKey) != null) {
                    evictionCount.incrementAndGet();
                }
            }
        }
    }

    /**
     * Get cache statistics
     */
    public CacheStats getStats() {
        return new CacheStats(
                hitCount.get(), missCount.get(), evictionCount.get(), cache.size(), validSize());
    }

    /**
     * Cache statistics container
     */
    public static class CacheStats {

        public final long hitCount;
        public final long missCount;
        public final long evictionCount;
        public final int totalSize;
        public final int validSize;
        public final double hitRatio;

        public CacheStats(
                long hitCount, long missCount, long evictionCount, int totalSize, int validSize) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.evictionCount = evictionCount;
            this.totalSize = totalSize;
            this.validSize = validSize;
            this.hitRatio = (hitCount + missCount) > 0 ? (double) hitCount / (hitCount + missCount) : 0.0;
        }

        @Override
        public String toString() {
            return String.format(
                    "CacheStats{hits=%d, misses=%d, evictions=%d, hitRatio=%.2f, size=%d/%d}",
                    hitCount, missCount, evictionCount, hitRatio, validSize, totalSize);
        }
    }
}
