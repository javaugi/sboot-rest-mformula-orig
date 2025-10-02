/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.CosmosIndexingPolicy;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import jakarta.persistence.Id;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@CosmosIndexingPolicy(includePaths = { "/eventId/?", "/processedAt/?" }, excludePaths = { "/*" })
@Container(containerName = "events")
public class EventRecord {

	@Id
	private String id; // uuid or composite key

	@PartitionKey
	private String eventId; // source event id for idempotency

	private String payload;

	private OffsetDateTime processedAt;

	// private Instant processedAt;

	// constructors/getters/setters

}
