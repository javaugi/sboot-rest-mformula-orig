/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
When to choose CompletableFuture: You need to orchestrate a complex workflow of asynchronous I/O tasks. It's more powerful and flexible
    than the older Future.get().
 */
public class CompletableFutureExample {

    // Simulate a remote API call
    public static CompletableFuture<String> getUserDetail(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate network latency
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            return "UserInfo for " + userId;
        });
    }

    // Simulate another remote API call
    public static CompletableFuture<Integer> getCreditRating(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
            }
            return 850;
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String userId = "123";

        // Kick off both async calls
        CompletableFuture<String> userInfoFuture = getUserDetail(userId);
        CompletableFuture<Integer> creditRatingFuture = getCreditRating(userId);

        // Combine them when both are done, without blocking.
        CompletableFuture<String> combinedFuture = userInfoFuture
            .thenCombine(creditRatingFuture, (userInfo, rating)
                -> userInfo + " has a credit rating of: " + rating
            );

        // At this point, the main thread is free to do other work.
        // We only block at the end to get the final result for demonstration.
        String result = combinedFuture.get(); // Blocks here until the final result is ready
        System.out.println(result);
        // Output: UserInfo for 123 has a credit rating of: 850
    }
}

/*
2. CompletableFuture: For Composable, Non-Blocking I/O Operations
    Use Case: CompletableFuture is the go-to choice for writing asynchronous, non-blocking code when you need to compose multiple asynchronous
        operations (e.g., call several microservices and combine their results). It provides a rich API for chaining, combining, and handling errors.

    Ideal for: Microservice communication, combining results from multiple databases or APIs, building asynchronous pipelines.

    Characteristics:
        I/O-Bound: Perfect for tasks that spend most of their time waiting for network or disk I/O.
        Composition: Excellent for defining a pipeline of "then do this, then do that."
        Non-Blocking: Allows you to write code that doesn't block threads while waiting for I/O, freeing them up for other work. It uses a default ForkJoinPool but you can easily supply your own Executor for I/O-heavy tasks.

Java Code Example: Calling Two Microservices and Combining Results
 */
