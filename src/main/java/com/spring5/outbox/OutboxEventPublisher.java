/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.outbox;

import static com.spring5.outbox.KafkaOutboxConfig.KAFKA_OUTBOX_TOPIC;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final @Qualifier("outboxKafkaTemplate")
    KafkaTemplate<String, Outbox> kafkaTemplate;

    /*
  For most Spring Boot/Spring applications, KafkaTemplate is the clear winner. It abstracts away the complexities of the
      native KafkaProducer while providing powerful features.
  Always prefer KafkaTemplate.send() for its simplicity, flexibility (you specify the topic), and integration with Spring's asynchronous features.
  Consider KafkaTemplate.sendDefault() if you have a primary topic and want to reduce boilerplate.
  Only use KafkaProducer directly if you are not using Spring or if you have a very specific, low-level requirement
      that KafkaTemplate cannot meet.
  Reserve KafkaTemplate.execute() for advanced scenarios where you need to interact directly with the KafkaProducer's methods
      that KafkaTemplate doesn't directly expose or for custom transaction management patterns.

  # Configure the default topic for KafkaTemplate
  spring.kafka.template.default-topic=my-default-topic
     */
    public void publish(Outbox event) {
        // my-default-topic
        this.kafkaTemplate.sendDefault(event);
        // or the specific kafka topic
        this.kafkaTemplate.send(KAFKA_OUTBOX_TOPIC, event.getAggregateId(), event);
        // Publish the event to message broker
    }
}

/*
KafkaTemplate, KafkaProducer, send, sendDefault execute - what to choose


When it comes to sending messages to Kafka in a Java application, you primarily choose between using the low-level KafkaProducer API provided by Apache Kafka or the higher-level KafkaTemplate provided by Spring for Apache Kafka. Each has its strengths and the "best" choice depends on your project's context and requirements.

Let's break them down:

1. org.apache.kafka.clients.producer.KafkaProducer
This is the core, low-level client provided directly by Apache Kafka.

What it is: The foundational class for producing records to Kafka topics. It handles all the nitty-gritty details of connecting to brokers, serialization, partitioning, batching, and error handling.

send() method:

Future<RecordMetadata> send(ProducerRecord<K, V> record): Sends a record asynchronously and returns a Future that can be used to get the RecordMetadata (topic, partition, offset, timestamp) or check for exceptions later.

Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback): Sends a record asynchronously and allows you to provide a Callback function that will be executed when the record is acknowledged by the broker (or an error occurs). This is generally the preferred way for handling success/failure asynchronously.

When to choose it:

Pure Kafka Client: If you're building a purely Kafka-centric application without Spring, or if you need absolute granular control over every aspect of the producer (e.g., custom buffer management, very specific error handling without Spring's abstractions).

Learning Kafka Internals: Great for understanding how the Kafka producer works at a fundamental level.

Minimal Dependencies: If you want to keep your project's dependencies to a minimum and avoid the Spring ecosystem.

Considerations:

Requires more manual configuration (e.g., producer properties, serializers).

You'll need to handle resource management (closing the producer) yourself.

Error handling and success callbacks are managed directly via Future or Callback interfaces.

2. org.springframework.kafka.core.KafkaTemplate
This is Spring's higher-level abstraction built on top of KafkaProducer. It simplifies Kafka integration in Spring-based applications.

What it is: A wrapper around a KafkaProducer instance that provides convenient methods for sending messages, integrates seamlessly with Spring's dependency injection, configuration, and transaction management.

send() methods:

CompletableFuture<SendResult<K, V>> send(String topic, V data)

CompletableFuture<SendResult<K, V>> send(String topic, K key, V data)

CompletableFuture<SendResult<K, V>> send(ProducerRecord<K, V> record)

And many other overloads for specifying partition, timestamp, etc.

Key difference: Returns a CompletableFuture (or ListenableFuture in older Spring Kafka versions), which is a more powerful and modern Java concurrency construct than a simple Future. This allows for easier chaining of asynchronous operations and callbacks.

sendDefault() methods:

CompletableFuture<SendResult<K, V>> sendDefault(V data)

CompletableFuture<SendResult<K, V>> sendDefault(K key, V data)

And other overloads.

Purpose: These methods are convenience methods that send messages to a default topic that you can configure in your KafkaTemplate. This avoids repeating the topic name for every send operation if your application primarily sends to one topic.

execute() method:

T execute(ProducerCallback<K, V, T> callback): This method allows you to execute arbitrary code within the scope of the underlying KafkaProducer. It's less commonly used for simple sends but is powerful for advanced scenarios where you need direct access to the KafkaProducer instance (e.g., for batching, transaction management, or low-level producer configuration that isn't exposed by KafkaTemplate directly). It provides the KafkaProducer instance as an argument to the ProducerCallback.

When to choose it:

Spring Applications: If your application is built with Spring Boot or Spring Framework, KafkaTemplate is the idiomatic choice. It leverages Spring's features for configuration, dependency injection, and auto-configuration, making setup much simpler.

Simplified API: Provides a more developer-friendly API for common sending operations.

Error Handling and Callbacks: Integrates well with Spring's asynchronous features (CompletableFuture) for handling send results. You can also configure a ProducerListener for global success/failure callbacks.

Transactional Kafka: If you need transactional message sending (atomically sending messages to Kafka and performing other database operations), KafkaTemplate provides excellent support for this.

Testing: Easier to mock and test in unit tests.

send, sendDefault, execute - What to Choose?
KafkaTemplate.send() (most common):

Choose this for 95% of your message sending needs.

It's the standard way to send messages to a specified Kafka topic.

You'll provide the topic name, key, and value (and optionally partition or timestamp) directly to the method call.

Example: kafkaTemplate.send("myTopic", "myKey", "myValue");

Returns a CompletableFuture<SendResult> for asynchronous handling of the send outcome.

KafkaTemplate.sendDefault() (convenience):

Choose this if your application sends messages primarily to a single Kafka topic.

Requires you to configure a defaultTopic on your KafkaTemplate instance.

Saves you from specifying the topic name repeatedly.

Example: kafkaTemplate.sendDefault("myKey", "myValue"); (assuming defaultTopic is set).

Also returns a CompletableFuture<SendResult>.

KafkaTemplate.execute() (advanced/specialized):

Rarely needed for simple message sending.

Choose this when you need direct, low-level access to the underlying KafkaProducer instance within a Spring-managed context. This is useful for:

Implementing custom batching logic not covered by standard KafkaTemplate methods.

Performing multiple producer operations within a single ProducerCallback.

Integrating with custom Kafka client features not directly exposed by KafkaTemplate.

When using transactional producers and you need to perform other operations like flush() or commitTransaction() in a specific order.

Example:

Java

kafkaTemplate.execute(producer -> {
    producer.send(new ProducerRecord<>("topic1", "key1", "value1"));
    producer.send(new ProducerRecord<>("topic2", "key2", "value2"));
    return "Messages sent via custom logic";
});
Summary and Recommendation:
    For most Spring Boot/Spring applications, KafkaTemplate is the clear winner. It abstracts away the complexities of the native KafkaProducer while providing powerful features.
    Always prefer KafkaTemplate.send() for its simplicity, flexibility (you specify the topic), and integration with Spring's asynchronous features.
    Consider KafkaTemplate.sendDefault() if you have a primary topic and want to reduce boilerplate.
    Only use KafkaProducer directly if you are not using Spring or if you have a very specific, low-level requirement that KafkaTemplate cannot meet.
    Reserve KafkaTemplate.execute() for advanced scenarios where you need to interact directly with the KafkaProducer's methods that KafkaTemplate doesn't directly expose or for custom transaction management patterns.
 */
