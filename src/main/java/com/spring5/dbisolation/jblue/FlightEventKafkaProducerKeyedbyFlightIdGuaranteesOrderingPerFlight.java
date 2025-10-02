/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FlightEventKafkaProducerKeyedbyFlightIdGuaranteesOrderingPerFlight {

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper mapper = new ObjectMapper();

	public FlightEventKafkaProducerKeyedbyFlightIdGuaranteesOrderingPerFlight(
			KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendEvent(FlightEvent event) throws Exception {
		String json = mapper.writeValueAsString(event);
		kafkaTemplate.send("flight-events", event.getId(), json);
	}

	/*
	 * 1. Kafka Ordering Rules Within a partition: Events sent to the same partition are
	 * appended sequentially to the log. Consumers always read them in the same order →
	 * ordering guaranteed per partition.
	 * 
	 * Across partitions: Events may be processed out of order across different partitions
	 * → no ordering guarantee.
	 * 
	 * 2. Why event.getId() Matters In your code: kafkaTemplate.send("flight-events",
	 * event.getId(), json); The key (event.getId()) determines the partition. Kafka
	 * ensures that all messages with the same key always go to the same partition.
	 * Ordering is preserved because Kafka writes messages in the order they arrive to
	 * each partition. So, for example: All events for flightId=123 go to Partition 1 in
	 * correct order. All events for flightId=456 may go to Partition 2 in correct order.
	 * But events for 123 and 456 can interleave because partitions process independently.
	 * 
	 * 3. Consumer Side Considerations On the consumer side, you must: Use one thread per
	 * partition to preserve ordering. If you add multiple consumer threads for the same
	 * partition, ordering breaks. Spring Kafka by default creates one listener container
	 * per partition if you set concurrency correctly:
	 * 
	 * @KafkaListener(topics = "flight-events", concurrency = "3") public void
	 * consumeFlightEvents(String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int
	 * partition) { System.out.println("Partition: " + partition + " Event: " + message);
	 * }
	 * 
	 * concurrency = number_of_partitions → one thread per partition. Each partition is
	 * consumed sequentially by one thread → ordering guaranteed per key.
	 * 
	 * 4. Visualizing Ordering Producer → Kafka Broker → Consumer | Key=123 | Partition 1
	 * → Ordered consumption | Key=456 | Partition 2 → Ordered consumption
	 * 
	 * 
	 * Events with the same key are written to the same partition in the order sent →
	 * consumed in order.
	 * 
	 * 5. Summary Table Requirement How to Achieve It Ordered processing per flightId Use
	 * flightId as message key → same partition → order preserved High throughput +
	 * parallelism Increase partitions → more consumers → order per key stays Strict
	 * global order across keys Not possible with Kafka at scale (use single partition →
	 * not scalable)
	 */

}
/*
 * Java Spring Boot Kafka Streams example that demonstrates Stateful aggregations (e.g.,
 * counting events per flight), Parallel processing (via partitions), Ordering guarantees
 * per key (using flightId as key).
 * 
 * Stateful Aggregations + Ordering Per Key We use flightId as key → ensures ordering
 * within each key. - FlightEventStreamOrderingPerKey Kafka Producer Example -
 * FlightEventKafkaProducerKeyedbyFlightIdGuaranteesOrderingPerFlight Send events keyed by
 * flightId → guarantees ordering per flight:
 * 
 * Parallel Processing Kafka partitions messages → multiple consumers → parallelism.
 * Ordering preserved within each partition per key. When creating the topic: kafka-topics
 * --create --topic flight-events --partitions 3 --replication-factor 1 --bootstrap-server
 * localhost:9092 3 partitions → parallelism Messages with same key → same partition →
 * ordering preserved.
 * 
 * This example: Consumes flight events Groups by flightId for ordering Counts events per
 * flight (stateful) Writes counts to another Kafka topic
 */
