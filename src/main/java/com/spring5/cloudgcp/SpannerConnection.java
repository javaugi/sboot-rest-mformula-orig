/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.cloudgcp;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Key; // For point reads
import com.google.cloud.spanner.KeySet; // For point reads
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TransactionRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpannerConnection {

	/*
	 * 1. Client initialization. 2. Inserting data using Mutations. 3. Querying data using
	 * SQL. 4. Reading specific data using Keys.
	 */

	// --- Configuration ---
	private static final String PROJECT_ID = "your-gcp-project-id"; // **REPLACE WITH YOUR
																	// PROJECT ID**

	private static final String INSTANCE_ID = "your-spanner-instance-id"; // **REPLACE
																			// WITH YOUR
																			// INSTANCE
																			// ID**

	private static final String DATABASE_ID = "your-spanner-database-id"; // **REPLACE
																			// WITH YOUR
																			// DATABASE
																			// ID**

	public static void main(String[] args) {
		// Initialize Spanner client
		SpannerOptions options = SpannerOptions.newBuilder().setProjectId(PROJECT_ID).build();
		Spanner spanner = options.getService(); // Spanner service client

		DatabaseClient dbClient = null; // Client for database operations

		try {
			// Get a database client for the specified database
			dbClient = spanner.getDatabaseClient(DatabaseId.of(PROJECT_ID, INSTANCE_ID, DATABASE_ID));
			System.out.println("Successfully connected to Spanner database: " + DATABASE_ID);

			// --- 1. Insert Data (using Mutations for Writes) ---
			System.out.println("\n--- Inserting Data ---");
			insertData(dbClient);

			// --- 2. Query Data (using SQL) ---
			System.out.println("\n--- Querying All Singers ---");
			queryData(dbClient);

			// --- 3. Read Data (using Keys for Point Reads) ---
			System.out.println("\n--- Reading a Specific Singer by Key ---");
			readSpecificData(dbClient, 1); // Read SingerId 1

			System.out.println("\n--- Operations Completed ---");

		}
		catch (SpannerException e) {
			System.err.println("Spanner error occurred: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			// It's crucial to close the Spanner client when done to release resources.
			// In a Spring Boot app, this would typically be managed by the Spring context
			// lifecycle.
			if (dbClient != null) {
				// DatabaseClient does not need explicit close, it's managed by Spanner
				// object
			}
			if (spanner != null) {
				spanner.close();
				System.out.println("Spanner client closed.");
			}
		}
	}

	/**
	 * Inserts new rows into the Singers table using a read/write transaction. Mutations
	 * are buffered and committed in a single atomic transaction.
	 */
	private static void insertData(DatabaseClient dbClient) {
		TransactionRunner runner = dbClient.readWriteTransaction();
		try {
			runner.run(transaction -> {
				List<Mutation> mutations = new ArrayList<>();
				mutations.add(Mutation.newInsertBuilder("Singers")
					.set("SingerId")
					.to(1L)
					.set("FirstName")
					.to("Alice")
					.set("LastName")
					.to("Wonder")
					.build());
				mutations.add(Mutation.newInsertBuilder("Singers")
					.set("SingerId")
					.to(2L)
					.set("FirstName")
					.to("Bob")
					.set("LastName")
					.to("Builder")
					.build());
				mutations.add(Mutation.newInsertBuilder("Singers")
					.set("SingerId")
					.to(3L)
					.set("FirstName")
					.to("Charlie")
					.set("LastName")
					.to("Chaplin")
					.build());
				transaction.buffer(mutations);
				System.out.println("Inserted 3 singers.");
				return null; // Commit the transaction
			});
		}
		catch (SpannerException e) {
			System.err.println("Failed to insert data: " + e.getMessage());
			throw e; // Re-throw to be caught by main's try-catch
		}
	}

	/**
	 * Queries all singers from the Singers table using a read-only transaction.
	 */
	private static void queryData(DatabaseClient dbClient) {
		// Use an explicit read-only transaction for strong consistency or a single-use
		// read for
		// freshest data. For simple queries, a single-use read is often sufficient.
		try (ResultSet resultSet = dbClient.singleUse()
			.executeQuery(Statement.of("SELECT SingerId, FirstName, LastName FROM Singers"))) {
			while (resultSet.next()) {
				long singerId = resultSet.getLong("SingerId");
				String firstName = resultSet.getString("FirstName");
				String lastName = resultSet.getString("LastName");
				System.out.printf("SingerId: %d, Name: %s %s%n", singerId, firstName, lastName);
			}
		}
		catch (SpannerException e) {
			System.err.println("Failed to query data: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Reads a specific singer by their primary key.
	 */
	private static void readSpecificData(DatabaseClient dbClient, long singerId) {
		try (ResultSet resultSet = dbClient.singleUse()
			.read("Singers", // Table name
					KeySet.singleKey(Key.of(singerId)), // Key(s) to read. For
														// multi-column primary keys, use
					// Key.of(col1, col2, ...)
					Arrays.asList("SingerId", "FirstName", "LastName"))) { // Columns to
																			// read
			if (resultSet.next()) {
				long id = resultSet.getLong("SingerId");
				String firstName = resultSet.getString("FirstName");
				String lastName = resultSet.getString("LastName");
				System.out.printf("Found SingerId: %d, Name: %s %s%n", id, firstName, lastName);
			}
			else {
				System.out.println("Singer with ID " + singerId + " not found.");
			}
		}
		catch (SpannerException e) {
			System.err.println("Failed to read specific data: " + e.getMessage());
			throw e;
		}
	}

}
