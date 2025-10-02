/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

/*
Key Walmart-Specific Problem Characteristics:
    Inventory Management: Knapsack-like optimization problems
    Logistics Optimization: Graph algorithms for route planning
    Pricing Strategy: Dynamic programming and game theory
    Supply Chain: Network flow and assignment problems
    Data Analysis: Big data processing and statistical forecasting
    Real-time Systems: Efficient algorithms for high-throughput data

These problems test candidates on:
    Algorithm design and optimization
    Data structures selection
    Problem decomposition
    Edge case handling
    Performance considerations for large-scale systems
 */
/**
 * Problem: Process millions of sales transactions efficiently Similar to Walmart's big
 * data processing requirements
 */
public class BigDataProcessing {

	public static Map<String, Integer> topSellingProducts(List<SaleRecord> sales, int topK) {
		// Use HashMap for O(1) lookups and min-heap for top K
		Map<String, Integer> productCounts = new HashMap<>();

		// Count sales per product
		for (SaleRecord sale : sales) {
			productCounts.put(sale.productId, productCounts.getOrDefault(sale.productId, 0) + sale.quantity);
		}

		// Use min-heap to get top K products
		PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

		for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
			minHeap.offer(entry);
			if (minHeap.size() > topK) {
				minHeap.poll();
			}
		}

		// Convert to result map
		Map<String, Integer> result = new LinkedHashMap<>();
		while (!minHeap.isEmpty()) {
			Map.Entry<String, Integer> entry = minHeap.poll();
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public static Map<String, Integer> topSellingProductsMy(List<SaleRecord> sales, int topK) {
		// Use HashMap for O(1) lookups and min-heap for top K
		Map<String, Integer> productCounts = new HashMap<>();

		// Count sales per product
		for (SaleRecord sale : sales) {
			productCounts.put(sale.productId, productCounts.getOrDefault(sale.productId, 0) + sale.quantity);
		}

		Map<String, List<SaleRecord>> grouped = sales.stream()
			.sorted(Comparator.comparing(SaleRecord::getProductId).thenComparing(SaleRecord::getQuantity).reversed())
			.collect(Collectors.groupingBy(SaleRecord::getProductId));

		// Convert to result map
		Map<String, Integer> result = new LinkedHashMap<>();
		int count = 0;
		for (String key : grouped.keySet()) {
			count++;
			result.put(key, productCounts.get(key));
			if (count >= topK) {
				break;
			}
		}

		return result;
	}

	public static Map<String, Integer> topSellingProductsImproved(List<SaleRecord> sales, int topK) {
		Map<String, Integer> result = new LinkedHashMap<>();

		Map<String, List<SaleRecord>> grouped = sales.stream()
			.sorted(Comparator.comparing(SaleRecord::getProductId).thenComparing(SaleRecord::getQuantity).reversed())
			.collect(Collectors.groupingBy(SaleRecord::getProductId));

		// Convert to result map
		int count = 0;
		for (String key : grouped.keySet()) {
			result.put(key, grouped.get(key).stream().mapToInt(r -> r.getQuantity()).sum());
			count++;
			if (count >= topK) {
				break;
			}
		}

		return result;
	}

	public static Map<String, Integer> topSellingProductsImproved2(List<SaleRecord> sales, int topK) {
		Map<String, Integer> result = new LinkedHashMap<>();

		Map<String, List<SaleRecord>> grouped = sales.stream()
			.sorted(Comparator.comparing(SaleRecord::getProductId).thenComparing(SaleRecord::getQuantity).reversed())
			.collect(Collectors.groupingBy(SaleRecord::getProductId));

		int count = 0;
		for (String key : grouped.keySet()) {
			result.put(key, grouped.get(key).stream().mapToInt(r -> r.getQuantity()).sum());
			count++;
			if (count >= topK) {
				break;
			}
		}

		/*
		 * result = result.entrySet().stream() .sorted((a, b) ->
		 * b.getValue().compareTo(a.getValue()))
		 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue,
		 * newValue) -> oldValue, // Merge function for duplicate keys (not relevant here)
		 * LinkedHashMap::new // Ensure insertion order is preserved ) );
		 * 
		 * result.entrySet().stream() .sorted(Map.Entry.<String,
		 * Integer>comparingByValue().reversed())
		 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue,
		 * newValue) -> oldValue, // Merge function for duplicate keys (not relevant here)
		 * LinkedHashMap::new // Ensure insertion order is preserved ) ).forEach((k, v) ->
		 * { });
		 * 
		 * Stream<Entry<String, Integer>> sEntry = result.entrySet().stream()
		 * .sorted(Map.Entry.<String, Integer>comparingByValue().reversed());
		 * result.entrySet().stream() .sorted(Map.Entry.<String,
		 * Integer>comparingByValue().reversed()) .forEach(entry -> { entry.getKey();
		 * entry.getValue(); }); //
		 */
		return result;
	}

	@Data
	@Builder(toBuilder = true)
	public static class SaleRecord {

		String productId;

		int quantity;

		double price;

		long timestamp;

		SaleRecord(String productId, int quantity, double price, long timestamp) {
			this.productId = productId;
			this.quantity = quantity;
			this.price = price;
			this.timestamp = timestamp;
		}

	}

	// Test with large dataset simulation
	public static void main(String[] args) {
		List<SaleRecord> sales = generateTestData(100);
		Map<String, Integer> topProducts = topSellingProducts(sales, 10);

		final AtomicInteger cnt = new AtomicInteger(0);
		System.out.println("1 topSellingProducts : Original Top 10 Selling Products:");
		for (String key : topProducts.keySet()) {
			System.out.println("Counting=" + cnt.incrementAndGet() + "   Original Product " + key + ": "
					+ topProducts.get(key) + " units");
		}

		System.out.println("2 topSellingProducts : Original Top 10 Selling Products:");
		cnt.set(0);
		topProducts.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue())
			.sorted((a, b) -> b.getValue() - a.getValue())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
					LinkedHashMap::new))
			.forEach((key, value) -> System.out
				.println("Counting=" + cnt.incrementAndGet() + "     Product " + key + ": " + value + " units"));

		System.out.println("topSellingProducts :Top 10 Selling Products:");
		cnt.set(0);
		topProducts.entrySet()
			.stream()
			.sorted((a, b) -> b.getValue() - a.getValue())
			.forEach(entry -> System.out.println("Counting=" + cnt.incrementAndGet() + "    Product " + entry.getKey()
					+ ": " + entry.getValue() + " units"));

		topProducts = topSellingProductsMy(sales, 10);
		System.out.println("3. topSellingProductsMy:  Looping 2 - Top 10 Selling Products:");
		cnt.set(0);
		topProducts.entrySet()
			.stream()
			.sorted((a, b) -> b.getValue() - a.getValue())
			.forEach(entry -> System.out.println("Counting=" + cnt.incrementAndGet() + "     Product " + entry.getKey()
					+ ": " + entry.getValue() + " units"));

		topProducts = topSellingProductsImproved(sales, 10);
		System.out.println("topSellingProductsImproved : Looping 3 - Top 10 Selling Products:");
		cnt.set(0);
		topProducts.entrySet()
			.stream()
			.sorted((a, b) -> b.getValue() - a.getValue())
			.forEach(entry -> System.out.println("Counting=" + cnt.incrementAndGet() + "   Product " + entry.getKey()
					+ ": " + entry.getValue() + " units"));

		topProducts = topSellingProductsImproved2(sales, 10);
		System.out.println("topSellingProductsImproved2 : Looping 3 - Top 10 Selling Products:");
		cnt.set(0);
		topProducts.forEach((key, value) -> System.out
			.println("Counting=" + cnt.incrementAndGet() + "     Product " + key + ": " + value + " units"));
	}

	private static List<SaleRecord> generateTestData(int size) {
		List<SaleRecord> sales = new ArrayList<>();
		Random random = new Random();
		String[] products = { "P001", "P002", "P003", "P004", "P005", "P006", "P007", "P008", "P009", "P010", "P011",
				"P012" };

		for (int i = 0; i < size; i++) {
			String productId = products[random.nextInt(products.length)];
			int quantity = 1 + random.nextInt(10);
			double price = 10 + random.nextDouble() * 100;
			long timestamp = System.currentTimeMillis() - random.nextInt(1000000);

			sales.add(new SaleRecord(productId, quantity, price, timestamp));
		}

		return sales;
	}

}
