/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.batch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author javaugi
 */
public class SingleForkedManyThreadRecordProcessor {

    private static final int TOTAL_RECORDS = 1000000;
    private static final int WORKER_THREADS = 10;
    private static final AtomicInteger recordsProcessed = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("Starting record processing...");

        // Single-threaded preparation phase
        prepareWork();

        // Multi-threaded processing phase
        ExecutorService executor = Executors.newFixedThreadPool(WORKER_THREADS);
        CountDownLatch latch = new CountDownLatch(WORKER_THREADS);

        // Divide work among threads
        int recordsPerThread = TOTAL_RECORDS / WORKER_THREADS;
        int remainingRecords = TOTAL_RECORDS % WORKER_THREADS;

        for (int i = 0; i < WORKER_THREADS; i++) {
            int start = i * recordsPerThread;
            int end = start + recordsPerThread + (i == WORKER_THREADS - 1 ? remainingRecords : 0);

            executor.submit(
                    () -> {
                        try {
                            processRecords(start, end);
                        } finally {
                            latch.countDown();
                        }
                    });
        }

        // Wait for all threads to complete
        try {
            latch.await();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nProcessing complete. Total records processed: " + recordsProcessed.get());
    }

    private static void prepareWork() {
        System.out.println("Single thread preparing work...");
        // Simulate preparation work
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Preparation complete. Forking to " + WORKER_THREADS + " threads...");
    }

    private static void processRecords(int start, int end) {
        for (int i = start; i < end; i++) {
            // Simulate record processing
            try {
                Thread.sleep(1); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            int processed = recordsProcessed.incrementAndGet();
            if (processed % 40000 == 0) {
                System.out.printf("\rProgress: %.1f%%", (processed * 100.0 / TOTAL_RECORDS));
            }
        }
        System.out.printf("Total processed" + (end - start));
    }
}
