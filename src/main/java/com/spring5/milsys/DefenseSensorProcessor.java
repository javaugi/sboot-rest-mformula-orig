/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.milsys;

import com.google.common.util.concurrent.RateLimiter;
import com.spring5.validatorex.DefenseSystemException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class DefenseSensorProcessor {

    // Thread-safe map for sensor data with atomic updates
    private final ConcurrentMap<String, AtomicReference<SensorReading>> sensorData
            = new ConcurrentHashMap<>();

    // Executor for parallel processing
    private final ExecutorService processingPool
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // Rate limiter to prevent system overload
    private final RateLimiter rateLimiter = RateLimiter.create(1000); // 1000 events/sec

    public static class SensorReading {

        private final String sensorId;
        private final double value;
        private final long timestamp;
        private final int reliabilityScore;

        public SensorReading(String sensorId, double value, long timestamp, int reliabilityScore) {
            this.sensorId = sensorId;
            this.value = value;
            this.timestamp = timestamp;
            this.reliabilityScore = reliabilityScore;
        }

        // Getters omitted for brevity
    }

    // Process incoming sensor data with thread safety and rate limiting
    public CompletableFuture<Void> processSensorData(SensorReading reading) {
        if (!rateLimiter.tryAcquire()) {
            return CompletableFuture.failedFuture(
                    new DefenseSystemException("Rate limit exceeded for sensor: " + reading.sensorId));
        }

        return CompletableFuture.runAsync(
                () -> {
                    // Atomic update of sensor data
                    sensorData.compute(
                            reading.sensorId,
                            (k, v) -> {
                                if (v == null
                                || reading.timestamp > v.get().timestamp
                                || reading.reliabilityScore > v.get().reliabilityScore) {
                                    return new AtomicReference<>(reading);
                                }
                                return v;
                            });

                    // Additional processing pipeline
                    try {
                        validateReading(reading);
                        checkAnomalies(reading);
                        notifySubsystems(reading);
                    } catch (DefenseSystemException ex) {

                    }
                },
                processingPool);
    }

    private void validateReading(SensorReading reading) throws DefenseSystemException {
        // Defense-specific validation logic
        if (reading.value < -1000 || reading.value > 1000) {
            throw new DefenseSystemException("Invalid sensor reading range: " + reading.value);
        }
    }

    private void checkAnomalies(SensorReading reading) throws DefenseSystemException {
    }

    private void notifySubsystems(SensorReading reading) throws DefenseSystemException {
    }

    // Additional methods omitted for brevity
    public void callApi(String requestData) {
        if (rateLimiter.tryAcquire()) {
            // Permit acquired! Proceed with the API call
            System.out.println("Calling API with data: " + requestData);
            // ... actual API call logic ...
        } else {
            // Permit not acquired immediately
            System.out.println(
                    "Rate limit exceeded. Could not call API for: " + requestData + ". Try again later.");
            // Optionally, queue the request or return an error
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DefenseSensorProcessor caller = new DefenseSensorProcessor();
        for (int i = 0; i < 10; i++) {
            caller.callApi("Request-" + i);
            // Simulate some quick successive calls
            if (i < 6) {
                Thread.sleep(50); // Less than the 200ms interval needed for 5 TPS
            } else {
                Thread.sleep(250); // Allow rate limiter to recover
            }
        }
    }

    public void whatDoRateLimitAndacquireLockFor() {
        // See main and callApi method

        /*
            com.google.common.util.concurrent.RateLimiter is a class from Google's Guava library that provides a way to control the rate at which some action or resource access occurs. It's a utility for limiting the frequency of events.

            Think of it like a turnstile or a token bucket:

            Turnstile: Allows only a certain number of people (actions) to pass through per unit of time.
            Token Bucket: You have a bucket that gets filled with tokens at a constant rate. To perform an action, you must acquire a token from the bucket. If the bucket is empty, you have to wait or you're denied.
            The primary purpose of RateLimiter is to:

            Prevent Overload: Protect systems or resources from being overwhelmed by too many requests in a short period. This can be crucial for maintaining stability and performance.
            Throttle Requests: Limit the number of API calls, database queries, network requests, or any other operation to stay within defined usage quotas or to prevent abuse.
            Smooth Out Bursts: While it allows for some burstiness (a temporary spike in requests), its main goal is to enforce an average rate over time.
            There are different types of RateLimiter implementations in Guava, with the most common being SmoothBursty, which allows for a certain number of permits to be saved up for bursts, and SmoothWarmingUp, which gradually increases the rate after a quiet period.

            rateLimiter.tryAcquire()
            The rateLimiter.tryAcquire() method is one of the ways to request permission (a "permit") from the RateLimiter to proceed with an action.

            Specifically, tryAcquire():

            Attempts to acquire a single permit from the RateLimiter.
            It's non-blocking: This is the key characteristic.
            If a permit is immediately available (meaning allowing the action wouldn't violate the configured rate), tryAcquire() takes the permit and returns true.
            If a permit is not immediately available, tryAcquire() does not wait. It immediately returns false.
            Use Cases: tryAcquire() is useful when you want to perform an action only if it can be done without delay according to the rate limit. If the action would exceed the rate, you might choose to:
            Skip the action.
            Queue the action for later.
            Return an error to the caller (e.g., "Too many requests, try again later").
            There are overloaded versions of tryAcquire():

            tryAcquire(int permits): Tries to acquire a specified number of permits without blocking. Returns true if all requested permits are immediately available, false otherwise.
            tryAcquire(long timeout, TimeUnit unit): Tries to acquire a single permit, waiting up to the specified timeout if necessary. Returns true if a permit was acquired within the timeout, false if the timeout elapsed before a permit could be acquired.
            tryAcquire(int permits, long timeout, TimeUnit unit): Tries to acquire a specified number of permits, waiting up to the specified timeout if necessary. Returns true if all requested permits were acquired within the timeout, false otherwise.
            In contrast to tryAcquire(), there's also an acquire() method:

            rateLimiter.acquire(): This method will block (wait) indefinitely until a permit becomes available. It's used when you must perform the action, and it's acceptable to wait until the rate limiter allows it.
            Example Scenario:

            Imagine you have an API that should not be called more than 5 times per second.

            Java

            import com.google.common.util.concurrent.RateLimiter;
            import java.util.concurrent.TimeUnit;

            public class ApiCaller {

                // Allow 5 permits per second
                private final RateLimiter rateLimiter = RateLimiter.create(5.0);

                public void callApi(String requestData) {
                    if (rateLimiter.tryAcquire()) {
                        // Permit acquired! Proceed with the API call
                        System.out.println("Calling API with data: " + requestData);
                        // ... actual API call logic ...
                    } else {
                        // Permit not acquired immediately
                        System.out.println("Rate limit exceeded. Could not call API for: " + requestData + ". Try again later.");
                        // Optionally, queue the request or return an error
                    }
                }

                public static void main(String[] args) throws InterruptedException {
                    ApiCaller caller = new ApiCaller();
                    for (int i = 0; i < 10; i++) {
                        caller.callApi("Request-" + i);
                        // Simulate some quick successive calls
                        if (i < 6) {
                             Thread.sleep(50); // Less than the 200ms interval needed for 5 TPS
                        } else {
                             Thread.sleep(250); // Allow rate limiter to recover
                        }
                    }
                }
            }
    In this example, tryAcquire() ensures that if too many calls are attempted in rapid succession, some will be skipped (or handled as an error) rather than overwhelming the target API or blocking the calling thread excessively.
         */
    }
}
