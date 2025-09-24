/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.spring.data.cosmos.core.mapping.CompositeIndex;
import com.azure.spring.data.cosmos.core.mapping.CompositeIndexPath;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.CosmosIndexingPolicy;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import com.azure.cosmos.models.CompositePathSortOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@CosmosIndexingPolicy(
    includePaths = {
        "/flightNumber/?",
        "/flightType/?",
        "/flightDate/?",
        "/price/?",
        "/airlineCode/?",
        "/departureTime/?",
        "/flightDuration/?"
    },
    excludePaths = {
        "/*"
    },
    compositeIndexes = {
        // For queries: ORDER BY flightNumber ASC, flightDate DESC
        @CompositeIndex(paths = {
        @CompositeIndexPath(path = "/flightNumber", order = CompositePathSortOrder.ASCENDING),
        @CompositeIndexPath(path = "/flightDate", order = CompositePathSortOrder.DESCENDING),
        @CompositeIndexPath(path = "/price", order = CompositePathSortOrder.DESCENDING),
        @CompositeIndexPath(path = "/flightDuration", order = CompositePathSortOrder.DESCENDING)
    }),
        // For queries: ORDER BY airlineCode ASC, departureTime DESC, flightNumber ASC
        @CompositeIndex(paths = {
        @CompositeIndexPath(path = "/airlineCode", order = CompositePathSortOrder.ASCENDING),
        @CompositeIndexPath(path = "/flightNumber", order = CompositePathSortOrder.ASCENDING),
        @CompositeIndexPath(path = "/flightDate", order = CompositePathSortOrder.DESCENDING),
        @CompositeIndexPath(path = "/price", order = CompositePathSortOrder.DESCENDING),
        @CompositeIndexPath(path = "/flightDuration", order = CompositePathSortOrder.DESCENDING)
    })
    }
)
@Container(containerName = "flightEvents", ru = "400")
public class FlightEvent {
    @Id
    private String id;

    @PartitionKey
    private String flightNumber; // flight-level unique key or flightId for each flight (used for ordering)
    /*
    The PartitionKey in Azure Cosmos DB Java container definition is a crucial concept that determines how your data is distributed and scaled.
        It's specified using the @PartitionKey annotation on the field that will serve as the partition key.

    Key Points About PartitionKey:
    1. Purpose:
        Horizontal scaling: Data is distributed across physical partitions based on partition key
        Performance: All operations within a partition are efficient and transactional
        Query efficiency: Point reads are fastest when you specify both ID and partition key
    4. Choosing a Good Partition Key:
        High cardinality: Many distinct values
        Even distribution: Similar number of items per partition key value
        Aligned with access patterns: Frequently queried together
    5. Multiple Partition Keys (if needed using synthetic keys):
     */
    // If you need a composite partition key, create a synthetic one:
    // Set it in constructor or method:
    /* 
    @PartitionKey
    private String partitionKey; // e.g., "UA_2024-01-15"
    public void setPartitionKey() {
        this.partitionKey = airlineCode + "_" + flightDate;
    }

    // Good partition key choices:
    @PartitionKey
    private String flightNumber; // High cardinality

    @PartitionKey
    private String airlineCode; // If you have many airlines

    // Poor partition key choices:
    @PartitionKey
    private String flightType; // Low cardinality (only few types: domestic/international)

    @PartitionKey
    private boolean isActive; // Very low cardinality (only true/false)
    // */

    private Map<String, Object> payload;
    private Double price;
    private Integer passengers;
    private String aircraftId;
    private String airlineCode;
    private String departureAirport;
    private String arrivalAirport;
    private String destination;
    private String flightType;
    private OffsetDateTime flightDate;
    private LocalDateTime departureTime;
    private Integer duration;
    private int delayMinutes;

    private String eventType; // "DEPARTURE", "ARRIVAL", "DELAY", etc.
    private String status; // e.g., Departed, Delayed, Arrived
    private Instant timestamp;
    private String details;
    private String airportCode;
    private String gate;

    private String description;

    @CreatedDate
    private OffsetDateTime createdDate;
    @LastModifiedDate
    private OffsetDateTime updatedDate;

    public FlightEvent() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }

        this.createdDate = OffsetDateTime.now();
        this.updatedDate = OffsetDateTime.now();

    }

    public FlightEvent(String id, String flightNumber, String eventType) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.eventType = eventType;
        this.timestamp = Instant.now();
    }

    public FlightEvent(String flightId, String flightNumber, String status, long timestamp) {
        this.id = flightId;
        this.flightNumber = flightNumber;
        this.status = status;
        this.timestamp = Instant.ofEpochMilli(timestamp);
    }


    @Override
    public String toString() {
        return String.format("FlightEvent{id='%s', number='%s', status='%s', ts=%d}",
            id, flightNumber, status, timestamp);
    }
}
