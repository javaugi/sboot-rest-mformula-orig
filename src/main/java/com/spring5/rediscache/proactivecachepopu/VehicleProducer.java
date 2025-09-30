/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * @author javau
 */
public class VehicleProducer {

    private final Producer<String, GenericRecord> producer;
    private final String topicName;
    private final Schema vehicleCreatedSchemaV1; // For demonstration of V1
    private final Schema vehicleCreatedSchemaV2; // For demonstration of V2

    public VehicleProducer(String bootstrapServers, String schemaRegistryUrl, String topicName)
            throws IOException {
        this.topicName = topicName;

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);

        this.producer = new KafkaProducer<>(props);

        // Load Avro schemas from resources
        try (InputStream isV1
                = getClass().getClassLoader().getResourceAsStream("avro/VehicleCreated_v1.avsc"); InputStream isV2
                = getClass().getClassLoader().getResourceAsStream("avro/VehicleCreated_v2.avsc")) {
            if (isV1 == null || isV2 == null) {
                throw new IOException("Avro schema files not found.");
            }
            this.vehicleCreatedSchemaV1 = new Schema.Parser().parse(isV1);
            this.vehicleCreatedSchemaV2 = new Schema.Parser().parse(isV2);
        }
    }

    public void sendVehicleCreatedEventV1(String vin, String make, String model, int year) {
        GenericRecord vehicleEvent = new GenericData.Record(vehicleCreatedSchemaV1);
        vehicleEvent.put("vin", vin);
        vehicleEvent.put("make", make);
        vehicleEvent.put("model", model);
        vehicleEvent.put("year", year);
        vehicleEvent.put("timestamp", System.currentTimeMillis());

        ProducerRecord<String, GenericRecord> record
                = new ProducerRecord<>(topicName, vin, vehicleEvent);

        producer.send(
                record,
                new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.printf(
                            "Successfully sent V1 event: VIN=%s, Topic=%s, Partition=%d, Offset=%d%n",
                            vin, metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void sendVehicleCreatedEventV2(
            String vin, String make, String model, int year, String color) {
        GenericRecord vehicleEvent = new GenericData.Record(vehicleCreatedSchemaV2);
        vehicleEvent.put("vin", vin);
        vehicleEvent.put("make", make);
        vehicleEvent.put("model", model);
        vehicleEvent.put("year", year);
        vehicleEvent.put("color", color); // This field is new in V2
        vehicleEvent.put("timestamp", System.currentTimeMillis());

        ProducerRecord<String, GenericRecord> record
                = new ProducerRecord<>(topicName, vin, vehicleEvent);

        producer.send(
                record,
                new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.printf(
                            "Successfully sent V2 event: VIN=%s, Topic=%s, Partition=%d, Offset=%d%n",
                            vin, metadata.topic(), metadata.partition(), metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void close() {
        producer.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String bootstrapServers = "localhost:9092"; // Your Kafka broker(s)
        String schemaRegistryUrl = "http://localhost:8081"; // Your Schema Registry URL
        String topic = "vehicle-inventory-events";

        VehicleProducer producer = new VehicleProducer(bootstrapServers, schemaRegistryUrl, topic);

        // Simulate sending V1 events (older producer)
        System.out.println("--- Sending V1 events ---");
        producer.sendVehicleCreatedEventV1("VIN12345", "Toyota", "Camry", 2020);
        Thread.sleep(1000);
        producer.sendVehicleCreatedEventV1("VIN67890", "Honda", "Civic", 2022);
        Thread.sleep(1000);

        // Simulate sending V2 events (newer producer)
        System.out.println("--- Sending V2 events ---");
        producer.sendVehicleCreatedEventV2("VIN11223", "Ford", "F-150", 2023, "Blue");
        Thread.sleep(1000);
        producer.sendVehicleCreatedEventV2("VIN44556", "Tesla", "Model 3", 2024, "Red");
        Thread.sleep(1000);

        producer.close();
    }
}
