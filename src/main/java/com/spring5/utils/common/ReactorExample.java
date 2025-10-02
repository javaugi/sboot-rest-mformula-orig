/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
When to choose Reactive: You are building a system where scalability and efficient resource usage under very high load are the primary concerns.
    You are dealing with streams of data and need to handle backpressure. This has a steeper learning curve but is the most powerful model for
    modern, high-scale applications.
 */
public class ReactorExample {

	// Simulate a database that returns a stream of results
	public static Flux<String> getDatabaseRecords() {
		// Imagine this connects to a DB and streams results
		return Flux.range(1, 1000) // Simulate 1000 records
			.map(i -> "Record_" + i);
	}

	public static void main(String[] args) throws InterruptedException {
		getDatabaseRecords()
			// Switch to a background scheduler for the I/O-bound operation
			.subscribeOn(Schedulers.boundedElastic())
			// Now, the subscriber can control the pace.
			// Let's simulate a slow subscriber that can only process 10 items at a time.
			.subscribe(record -> {
				// Process each record
				System.out.println("Processing: " + record);
				try {
					Thread.sleep(10);
				} // Simulate slow processing
				catch (InterruptedException e) {
				}
			}, error -> System.err.println("Error: " + error), // Error handler
					() -> System.out.println("Stream completed!"), // Completion handler
					// Backpressure request: initially ask for 10 items
					subscription -> subscription.request(10));

		// Keep the main thread alive long enough to see output
		Thread.sleep(5000);
	}

}

/*
 * 3. Reactive (Project Reactor): For High-Concurrency, Streaming Data Use Case: The
 * Reactive Streams specification (implemented by Project Reactor) is designed for
 * high-throughput, low-latency systems that need to handle backpressure. It's ideal for
 * streaming data and scenarios with very high concurrency (thousands or millions of
 * concurrent connections).
 * 
 * Ideal for: Real-time data feeds, chat applications, streaming large datasets, high-load
 * microservices, and any scenario where backpressure (controlling the data flow from a
 * fast producer to a slow consumer) is critical.
 * 
 * Characteristics: Event-Driven & Non-Blocking: From top to bottom. Backpressure: The
 * core concept that allows a subscriber to control the rate at which a publisher sends
 * data, preventing it from being overwhelmed. Lazy: Nothing happens until you
 * subscribe(). Functional Fluent API: Similar to Java Streams but for asynchronous data
 * streams.
 * 
 * Java Code Example (Project Reactor): Streaming a Database Result with Backpressure
 */
