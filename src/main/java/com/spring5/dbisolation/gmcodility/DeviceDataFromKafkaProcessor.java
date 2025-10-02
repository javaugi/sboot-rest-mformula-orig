/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeviceDataFromKafkaProcessor {

	private final KafkaConsumer<String, String> consumer;

	private final Map<String, Queue<Double>> deviceWindows;

	private final int windowSize;

	private final ExecutorService executor;

	public DeviceDataFromKafkaProcessor(String bootstrapServers, String topic, int windowSize) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "device-data-processor");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		this.consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic));
		this.deviceWindows = new ConcurrentHashMap<>();
		this.windowSize = windowSize;
		this.executor = Executors.newFixedThreadPool(4);
	}

	public void process() {
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

				for (ConsumerRecord<String, String> record : records) {
					executor.submit(() -> processRecord(record));
				}
			}
		}
		finally {
			consumer.close();
			executor.shutdown();
		}
	}

	private void processRecord(ConsumerRecord<String, String> record) {
		try {
			String deviceId = record.key();
			double value = Double.parseDouble(record.value());

			deviceWindows.compute(deviceId, (k, queue) -> {
				if (queue == null) {
					queue = new ArrayDeque<>(windowSize);
				}
				if (queue.size() == windowSize) {
					queue.poll();
				}
				queue.add(value);
				return queue;
			});

			double average = calculateAverage(deviceWindows.get(deviceId));
			System.out.printf("Device %s - Rolling average: %.2f%n", deviceId, average);

		}
		catch (NumberFormatException e) {
			System.err.println("Invalid data format: " + record.value());
		}
	}

	private double calculateAverage(Queue<Double> values) {
		return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
	}

	public static void main(String[] args) {
		DeviceDataFromKafkaProcessor processor = new DeviceDataFromKafkaProcessor("kafka-broker:9092", "device-metrics",
				10);
		processor.process();
	}

}
