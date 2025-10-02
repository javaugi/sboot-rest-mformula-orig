/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

/*
Key Differences

Feature         Kafka Partitions                            Redis Partitions (Sharding)
Purpose         Parallel message consumption                Data distribution across nodes
Ordering	Ordered within a partition                  No ordering guarantees across shards
Implementation	Built into Kafka                            Client-side or Redis Cluster
Rebalancing	Automatic consumer rebalancing              Manual or Redis Cluster management
Data Locality	All brokers have all partitions (replicas)  Data exists only on specific shard

This example shows how Kafka partitions ensure all orders for the same customer are processed in order, while Redis partitions distribute the storage load across multiple instances.
 */
public class WithPartitionConfig {

	public void concurrentProcessing() {
		/*
		 * Configuring ConcurrentKafkaListenerContainerFactory for Concurrent Processing
		 * Here's a complete example of how to configure and use
		 * ConcurrentKafkaListenerContainerFactory for concurrent message processing in
		 * Spring Boot with Kafka.
		 * 
		 * 1. Basic Configuration First, let's set up the basic configuration for
		 * concurrent processing:
		 * 
		 * java import org.apache.kafka.clients.consumer.ConsumerConfig; import
		 * org.apache.kafka.common.serialization.StringDeserializer; import
		 * org.springframework.beans.factory.annotation.Value; import
		 * org.springframework.context.annotation.Bean; import
		 * org.springframework.context.annotation.Configuration; import
		 * org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
		 * import org.springframework.kafka.core.ConsumerFactory; import
		 * org.springframework.kafka.core.DefaultKafkaConsumerFactory; import
		 * org.springframework.kafka.listener.ContainerProperties;
		 * 
		 * import java.util.HashMap; import java.util.Map;
		 * 
		 * @Configuration public class KafkaConfig {
		 * 
		 * @Value("${spring.kafka.bootstrap-servers}") private String bootstrapServers;
		 * 
		 * @Value("${spring.kafka.consumer.group-id}") private String groupId;
		 * 
		 * @Value("${spring.kafka.consumer.concurrency:3}") // Default to 3 consumers
		 * private int concurrency;
		 * 
		 * // Consumer configuration
		 * 
		 * @Bean public Map<String, Object> consumerConfigs() { Map<String, Object> props
		 * = new HashMap<>(); props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
		 * bootstrapServers); props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		 * props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
		 * StringDeserializer.class);
		 * props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
		 * StringDeserializer.class); props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
		 * "earliest"); props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); //
		 * Manual commit props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // Max
		 * records per poll return props; }
		 * 
		 * // Consumer factory
		 * 
		 * @Bean public ConsumerFactory<String, String> consumerFactory() { return new
		 * DefaultKafkaConsumerFactory<>(consumerConfigs()); }
		 * 
		 * // Concurrent listener container factory
		 * 
		 * @Bean public ConcurrentKafkaListenerContainerFactory<String, String>
		 * kafkaListenerContainerFactory() {
		 * ConcurrentKafkaListenerContainerFactory<String, String> factory = new
		 * ConcurrentKafkaListenerContainerFactory<>();
		 * factory.setConsumerFactory(consumerFactory());
		 * factory.setConcurrency(concurrency); // Number of concurrent consumers
		 * factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL)
		 * ; // Manual commit factory.getContainerProperties().setPollTimeout(3000); // 3
		 * seconds return factory; } } 2. Listener Implementation with Concurrent
		 * Processing Here's how to implement a listener that takes advantage of
		 * concurrent processing:
		 * 
		 * java import org.apache.kafka.clients.consumer.ConsumerRecord; import
		 * org.slf4j.Logger; import org.slf4j.LoggerFactory; import
		 * org.springframework.kafka.annotation.KafkaListener; import
		 * org.springframework.kafka.support.Acknowledgment; import
		 * org.springframework.stereotype.Service;
		 * 
		 * @Service public class KafkaConsumerService {
		 * 
		 * private static final Logger logger =
		 * LoggerFactory.getLogger(KafkaConsumerService.class);
		 * 
		 * // Basic concurrent listener
		 * 
		 * @KafkaListener( topics = "${kafka.topic.consumer}", containerFactory =
		 * "kafkaListenerContainerFactory" ) public void consume(ConsumerRecord<String,
		 * String> record, Acknowledgment ack) { try { logger.
		 * info("Received message on thread {}: key={}, value={}, partition={}, offset={}"
		 * , Thread.currentThread().getName(), record.key(), record.value(),
		 * record.partition(), record.offset());
		 * 
		 * // Process your message here processMessage(record.value());
		 * 
		 * // Manually acknowledge the message ack.acknowledge(); } catch (Exception e) {
		 * logger.error("Error processing message: {}", record.value(), e); // Handle
		 * error (e.g., send to DLQ) } }
		 * 
		 * private void processMessage(String message) { // Your business logic here
		 * logger.info("Processing message: {}", message); } } 3. Advanced Configuration
		 * Options Here are some advanced configuration options you might want to add:
		 * 
		 * 3.1. Error Handling and Retry java
		 * 
		 * @Bean public ConcurrentKafkaListenerContainerFactory<String, String>
		 * kafkaListenerContainerFactory() {
		 * ConcurrentKafkaListenerContainerFactory<String, String> factory = new
		 * ConcurrentKafkaListenerContainerFactory<>();
		 * factory.setConsumerFactory(consumerFactory());
		 * factory.setConcurrency(concurrency);
		 * 
		 * // Configure error handler factory.setErrorHandler(new
		 * SeekToCurrentErrorHandler( new DeadLetterPublishingRecoverer(kafkaTemplate),
		 * new FixedBackOff(1000L, 2L) // 1 second interval, 2 attempts ));
		 * 
		 * // Configure retry template RetryTemplate retryTemplate = new RetryTemplate();
		 * ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		 * backOffPolicy.setInitialInterval(1000); backOffPolicy.setMultiplier(2.0);
		 * backOffPolicy.setMaxInterval(10000);
		 * retryTemplate.setBackOffPolicy(backOffPolicy); retryTemplate.setRetryPolicy(new
		 * SimpleRetryPolicy(3));
		 * 
		 * factory.setRetryTemplate(retryTemplate);
		 * 
		 * return factory; } 3.2. Batch Processing java
		 * 
		 * @Bean public ConcurrentKafkaListenerContainerFactory<String, String>
		 * batchKafkaListenerContainerFactory() {
		 * ConcurrentKafkaListenerContainerFactory<String, String> factory = new
		 * ConcurrentKafkaListenerContainerFactory<>();
		 * factory.setConsumerFactory(consumerFactory());
		 * factory.setConcurrency(concurrency); factory.setBatchListener(true); // Enable
		 * batch processing
		 * 
		 * // Configure batch properties
		 * factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
		 * factory.getContainerProperties().setIdleEventInterval(10000L); // 10 seconds
		 * 
		 * return factory; }
		 * 
		 * // Batch listener implementation
		 * 
		 * @KafkaListener( topics = "${kafka.topic.batch}", containerFactory =
		 * "batchKafkaListenerContainerFactory" ) public void
		 * consumeBatch(List<ConsumerRecord<String, String>> records, Acknowledgment ack)
		 * { logger.info("Received batch of {} messages", records.size());
		 * 
		 * for (ConsumerRecord<String, String> record : records) { try {
		 * processMessage(record.value()); } catch (Exception e) {
		 * logger.error("Error processing message in batch: {}", record.value(), e); } }
		 * 
		 * ack.acknowledge(); // Acknowledge the entire batch } 4. Dynamic Concurrency
		 * Adjustment You can dynamically adjust concurrency based on load:
		 * 
		 * java
		 * 
		 * @Service public class ConcurrencyManager {
		 * 
		 * @Autowired private ConcurrentKafkaListenerContainerFactory<String, String>
		 * containerFactory;
		 * 
		 * @Autowired private KafkaListenerEndpointRegistry registry;
		 * 
		 * public void adjustConcurrency(String listenerId, int newConcurrency) {
		 * MessageListenerContainer container = registry.getListenerContainer(listenerId);
		 * if (container instanceof ConcurrentMessageListenerContainer) {
		 * ((ConcurrentMessageListenerContainer<?, ?>)
		 * container).setConcurrency(newConcurrency);
		 * logger.info("Adjusted concurrency for listener {} to {}", listenerId,
		 * newConcurrency); } } }
		 * 
		 * // Then in your listener:
		 * 
		 * @KafkaListener( id = "myListener", // Important to set an ID for dynamic
		 * control topics = "${kafka.topic.consumer}", containerFactory =
		 * "kafkaListenerContainerFactory" ) public void
		 * consumeWithDynamicConcurrency(ConsumerRecord<String, String> record) { // Your
		 * processing logic } 5. Partition Assignment Strategy Configure custom partition
		 * assignment:
		 * 
		 * java
		 * 
		 * @Bean public ConcurrentKafkaListenerContainerFactory<String, String>
		 * kafkaListenerContainerFactory() {
		 * ConcurrentKafkaListenerContainerFactory<String, String> factory = new
		 * ConcurrentKafkaListenerContainerFactory<>();
		 * factory.setConsumerFactory(consumerFactory());
		 * factory.setConcurrency(concurrency);
		 * 
		 * // Custom partition assignment strategy
		 * factory.getContainerProperties().setConsumerRebalanceListener(new
		 * ConsumerRebalanceListener() {
		 * 
		 * @Override public void onPartitionsRevoked(Collection<TopicPartition>
		 * partitions) { logger.info("Partitions revoked: {}", partitions); }
		 * 
		 * @Override public void onPartitionsAssigned(Collection<TopicPartition>
		 * partitions) { logger.info("Partitions assigned: {}", partitions); } });
		 * 
		 * return factory; } Key Considerations for Concurrent Processing Partition Count:
		 * The maximum effective concurrency is limited by the number of partitions in the
		 * topic
		 * 
		 * Ordering Guarantees: Messages within a partition are processed in order, but
		 * messages across partitions may be processed out of order
		 * 
		 * Resource Utilization: Balance concurrency with available system resources
		 * 
		 * Thread Safety: Ensure your message processing logic is thread-safe
		 * 
		 * Error Handling: Implement robust error handling for concurrent scenarios
		 * 
		 * This configuration provides a flexible foundation for concurrent Kafka message
		 * processing in Spring Boot, allowing you to scale your consumers horizontally
		 * while maintaining control over message processing behavior.
		 */
	}

	public void redisKafkaPartions() {
		/*
		 * Kafka Partitions vs Redis Partitions: Examples and Code Kafka Partitions Kafka
		 * partitions are how Kafka scales and parallelizes topic consumption. Each
		 * partition is an ordered, immutable sequence of records.
		 * 
		 * Kafka Partition Example 1. Creating a Topic with Partitions (Admin API):
		 * 
		 * java import org.apache.kafka.clients.admin.*;
		 * 
		 * AdminClient admin = AdminClient.create(properties); NewTopic newTopic = new
		 * NewTopic("orders", 3, (short) 1); // 3 partitions, replication factor 1
		 * 
		 * CreateTopicsResult result =
		 * admin.createTopics(Collections.singleton(newTopic)); result.all().get(); //
		 * Wait for topic creation 2. Producing Messages with Partition Keys:
		 * 
		 * java
		 * 
		 * @Autowired private KafkaTemplate<String, String> kafkaTemplate;
		 * 
		 * public void sendOrderEvent(OrderEvent event) { // Use orderId as key to ensure
		 * same order always goes to same partition kafkaTemplate.send("orders",
		 * event.getOrderId(), event.toString());
		 * 
		 * // Or explicitly specify partition // kafkaTemplate.send("orders", 0,
		 * event.getOrderId(), event.toString()); } 3. Consuming from Specific Partitions:
		 * 
		 * java
		 * 
		 * @KafkaListener(topicPartitions = {
		 * 
		 * @TopicPartition(topic = "orders", partitions = { "0", "1" }), // Listen to
		 * partitions 0 and 1
		 * 
		 * @TopicPartition(topic = "alerts", partitions = "0") }) public void
		 * listenToPartitions(ConsumerRecord<String, String> record) {
		 * System.out.printf("Received from partition %d: %s%n", record.partition(),
		 * record.value()); } Redis Partitions Redis partitions (shards) divide data
		 * across multiple Redis instances. Unlike Kafka's built-in partitioning, Redis
		 * partitioning typically requires client-side or proxy-based implementation.
		 * 
		 * Redis Partitioning Examples 1. Client-side Partitioning:
		 * 
		 * java import redis.clients.jedis.*;
		 * 
		 * // Create connection pools for each Redis instance JedisPool pool1 = new
		 * JedisPool("redis1.example.com", 6379); JedisPool pool2 = new
		 * JedisPool("redis2.example.com", 6379);
		 * 
		 * public Jedis getShard(String key) { // Simple hash-based partitioning int hash
		 * = Math.abs(key.hashCode()); return (hash % 2 == 0) ? pool1.getResource() :
		 * pool2.getResource(); }
		 * 
		 * public void setData(String key, String value) { try (Jedis jedis =
		 * getShard(key)) { jedis.set(key, value); } } 2. Using Redis Cluster (Server-side
		 * Partitioning):
		 * 
		 * java import redis.clients.jedis.JedisCluster; import
		 * redis.clients.jedis.HostAndPort;
		 * 
		 * Set<HostAndPort> clusterNodes = new HashSet<>(); clusterNodes.add(new
		 * HostAndPort("redis1.example.com", 6379)); clusterNodes.add(new
		 * HostAndPort("redis2.example.com", 6379)); clusterNodes.add(new
		 * HostAndPort("redis3.example.com", 6379));
		 * 
		 * JedisCluster jedisCluster = new JedisCluster(clusterNodes);
		 * 
		 * // Automatically routes to correct shard jedisCluster.set("order:123",
		 * "order_data"); String value = jedisCluster.get("order:123"); 3. Consistent
		 * Hashing Example:
		 * 
		 * java import com.google.common.hash.Hashing;
		 * 
		 * public class RedisPartitioner { private List<JedisPool> pools;
		 * 
		 * public RedisPartitioner(List<String> redisHosts) { pools = redisHosts.stream()
		 * .map(host -> new JedisPool(host, 6379)) .collect(Collectors.toList()); }
		 * 
		 * public Jedis getConnection(String key) { int hash = Hashing.consistentHash(
		 * Hashing.sha256().hashString(key, StandardCharsets.UTF_8), pools.size() );
		 * return pools.get(hash).getResource(); } } Key Differences Feature Kafka
		 * Partitions Redis Partitions (Sharding) Purpose Parallel message consumption
		 * Data distribution across nodes Ordering Ordered within a partition No ordering
		 * guarantees across shards Implementation Built into Kafka Client-side or Redis
		 * Cluster Rebalancing Automatic consumer rebalancing Manual or Redis Cluster
		 * management Data Locality All brokers have all partitions (replicas) Data exists
		 * only on specific shard Practical Use Case Example Order Processing System:
		 * 
		 * java // Kafka Producer - partitions orders by customer ID public void
		 * processOrder(Order order) { // Send to partition based on customerId for
		 * ordering kafkaTemplate.send("orders", order.getCustomerId(), order.toJson());
		 * 
		 * // Store in Redis - sharded by orderId try (Jedis jedis =
		 * redisPartitioner.getConnection(order.getId())) { jedis.setex("order:" +
		 * order.getId(), 3600, order.toJson()); } }
		 * 
		 * // Kafka Consumer - processes orders concurrently
		 * 
		 * @KafkaListener(topics = "orders", concurrency = "3") public void
		 * handleOrder(String orderJson, Acknowledgment ack) { Order order =
		 * Order.fromJson(orderJson);
		 * 
		 * try (Jedis jedis = redisPartitioner.getConnection(order.getId())) { // Check if
		 * order was already processed if (jedis.exists("processed:" + order.getId())) {
		 * return; }
		 * 
		 * // Process order orderService.process(order);
		 * 
		 * // Mark as processed jedis.setex("processed:" + order.getId(), 86400, "1");
		 * ack.acknowledge(); } } This example shows how Kafka partitions ensure all
		 * orders for the same customer are processed in order, while Redis partitions
		 * distribute the storage load across multiple instances.
		 */
	}

}
