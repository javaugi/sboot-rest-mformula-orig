/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.models.CompositePathSortOrder;
import com.azure.spring.data.cosmos.core.mapping.CompositeIndex;
import com.azure.spring.data.cosmos.core.mapping.CompositeIndexPath;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.CosmosIndexingPolicy;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
        excludePaths = {"/*"},
        compositeIndexes = {
            // For queries: ORDER BY flightNumber ASC, flightDate DESC
            @CompositeIndex(
                    paths = {
                        @CompositeIndexPath(path = "/flightNumber", order = CompositePathSortOrder.ASCENDING),
                        @CompositeIndexPath(path = "/flightDate", order = CompositePathSortOrder.DESCENDING),
                        @CompositeIndexPath(path = "/price", order = CompositePathSortOrder.DESCENDING),
                        @CompositeIndexPath(path = "/flightDuration", order = CompositePathSortOrder.DESCENDING)
                    }),
            // For queries: ORDER BY airlineCode ASC, departureTime DESC, flightNumber ASC
            @CompositeIndex(
                    paths = {
                        @CompositeIndexPath(path = "/airlineCode", order = CompositePathSortOrder.ASCENDING),
                        @CompositeIndexPath(path = "/flightNumber", order = CompositePathSortOrder.ASCENDING),
                        @CompositeIndexPath(path = "/flightDate", order = CompositePathSortOrder.DESCENDING),
                        @CompositeIndexPath(path = "/price", order = CompositePathSortOrder.DESCENDING),
                        @CompositeIndexPath(path = "/flightDuration", order = CompositePathSortOrder.DESCENDING)
                    })
        })
@Container(containerName = "bookingEvents", ru = "400")
public class BookingEvent {

    private String id; // unique event id (UUID)

    @PartitionKey
    private String bookingId; // booking-level key (used for ordering)
    private String flightId;
    private String userId;
    private String action; // BOOKED, CONFIRMED, CANCELLED
    private Instant timestamp;
    private Map<String, Object> payload;
}
