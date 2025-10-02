/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StoreIdPartitioner implements Partitioner {

	private final ConcurrentMap<String, Integer> partitionCache = new ConcurrentHashMap<>();

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

		String storeId = extractStoreId(key, value);

		// Get all partitions for the topic
		List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
		int numPartitions = partitions.size();

		if (numPartitions == 0) {
			return 0;
		}

		// Consistent hashing for same storeId always goes to same partition
		int partition = Math.abs(storeId.hashCode()) % numPartitions;

		log.debug("Partitioning message for storeId: {} to partition: {}", storeId, partition);
		return partition;
	}

	private String extractStoreId(Object key, Object value) {
		// Priority 1: Use key if it's the storeId
		if (key instanceof String) {
			return (String) key;
		}

		// Priority 2: Extract storeId from message value
		if (value instanceof StoreTransaction) {
			return ((StoreTransaction) value).getStoreId();
		}

		// Fallback: Use hash of entire object
		return String.valueOf(Objects.hash(key, value));
	}

	@Override
	public void close() {
		partitionCache.clear();
	}

	@Override
	public void configure(Map<String, ?> configs) {
		// Additional configuration if needed
	}

}
