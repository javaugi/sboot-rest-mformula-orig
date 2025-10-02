/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

/**
 * @author javau
 */
public class FlightServiceQueryOptimization {

	/*
	 * Query tips: • Use point reads (container.readItem(id, pk, Class)) when possible —
	 * they are cheap (1 RU). • Prefer ORDER BY on indexed fields and avoid ORDER BY
	 * across partitions if possible. • Use projections (select specific fields) rather
	 * than SELECT *. • Avoid server-side JOINs across many items; use client-side
	 * composition or pre-join data. • Use maxItemCount smaller when scanning many results
	 * to reduce RU spikes and allow parallel processing with continuation tokens.
	 */

	// 1. Client-Side Composition Approach: Service Layer with Client-Side Composition:
	// 2. 2. Pre-Joined Data (Denormalization) Approach: Denormalized Data Model:
	// FlightDetail and
	// Service with Pre-Joined Data:
	// 3. Hybrid Approach with Change Feed for Pre-joining
	/*
	 * Key Benefits: ✅ Single Query: Get all related data with one query ✅ Better
	 * Performance: No server-side JOIN overhead ✅ Scalable: Each document is independent
	 * ✅ Predictable RU cost: Single read/write operation
	 * 
	 * When to Use Which: Client-side composition: When data changes frequently Pre-joined
	 * data: When read performance is critical and data doesn't change often Hybrid
	 * approach: Best of both worlds, but more complex to implement
	 */
	/*
	 * 1. Using Projections (Select Specific Fields) Repository with Projection Methods:
	 * Service Layer Using Projections: 2. Using maxItemCount for Pagination and Parallel
	 * Processing Repository with Pagination Support: Implementation with maxItemCount and
	 * Continuation Tokens: Service Layer with Controlled Pagination:
	 * 
	 * Key Benefits: Projections: ✅ Reduced RU consumption: Smaller payloads ✅ Better
	 * performance: Less data transferred ✅ Faster queries: Database returns only needed
	 * fields
	 * 
	 * maxItemCount and Pagination: ✅ Avoid RU spikes: Controlled request size ✅ Memory
	 * efficiency: Process data in chunks ✅ Parallel processing: Handle large datasets
	 * efficiently ✅ Resumable operations: Continuation tokens allow pausing/resuming
	 */

}
