/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.jcacheimpl;

import java.util.concurrent.TimeUnit;

public class TTLCacheExample {

    public static void main(String[] args) throws InterruptedException {
        // Create cache with 2-second TTL and max 100 entries
        TTLCache<String, String> cache = new TTLCache<>(2, TimeUnit.SECONDS, 100);

        // Basic put and get
        cache.put("key1", "value1");
        System.out.println("Get key1: " + cache.get("key1")); // Returns "value1"

        // Wait for expiration
        Thread.sleep(2500);
        System.out.println("Get key1 after TTL: " + cache.get("key1")); // Returns null

        // Cache-through pattern
        String value = cache.get("key2", () -> {
            System.out.println("Computing value for key2...");
            return "computed_value";
        });
        System.out.println("Computed value: " + value);

        // Get statistics
        System.out.println("Cache stats: " + cache.getStats());

        // Custom TTL
        cache.put("key3", "value3", 5, TimeUnit.SECONDS);

        // Shutdown when done
        cache.shutdown();
    }
}

/*
Key Features
    Thread Safety: Uses ConcurrentHashMap and atomic operations for thread safety
    TTL Support: Automatic expiration of entries based on time-to-live
    LRU Eviction: Least Recently Used eviction when capacity is reached
    Periodic Cleanup: Background thread removes expired entries
    Cache-Through Pattern: Optional value loader for computing values on cache miss
    Statistics Tracking: Hit/miss counts, eviction tracking, and hit ratio
    Capacity Management: Enforces maximum size limits

Design Considerations
    Memory Efficiency: Uses separate structures for storage and access tracking
    Performance: Minimizes locking and uses efficient concurrent collections
    Cleanup Strategy: Background cleanup with configurable interval balances performance and memory usage
    LRU Implementation: Simple but effective LRU using a concurrent deque
    Graceful Shutdown: Proper executor service shutdown mechanism

This implementation provides a robust, self-contained caching solution without external dependencies, suitable for most Java 8 applications.
*/
