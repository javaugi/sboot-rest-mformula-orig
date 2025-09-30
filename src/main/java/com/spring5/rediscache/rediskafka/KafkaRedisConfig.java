/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import com.spring5.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaRedisConfig extends KafkaBaseConfig {

    public static final String REDIS_KAFKA_ORDER_EVENT_TMPL = "redisKafkaEventKafkaTemplate";

    @Bean(name = REDIS_KAFKA_ORDER_EVENT_TMPL)
    public KafkaTemplate<String, KafkaOrderedEvent> redisKafkaEventKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(baseAvroProducerConfigs()));
    }
}
/*
Example:
@RestController
@RequestMapping("/api/medications")
public class MedicationController {
private final MedicationService service;
@GetMapping("/{id}")
public CompletableFuture<Medication> getMedication(@PathVariable String id) {
return CompletableFuture.supplyAsync(() -> service.getMedication(id));
}
}
java Copy Download
2. How would you handle transaction management in a pharmacy order system?
Use @Transactional with proper isolation levels.
Example:
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class OrderService {
public void processOrder(Order order) {
// Deduct inventory
// Process payment
// Log audit trail
}
}
3. How do you secure a Spring Boot API for HIPAA compliance?
Use OAuth2 with JWT, encrypt PHI data, and enforce RBAC.
Example:
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
@Override
protected void configure(HttpSecurity http) throws Exception {
http
.authorizeRequests()
.antMatchers("/api/patients/**").hasRole("PHARMACIST")
.and()
.oauth2ResourceServer()
.jwt();
java Copy Download
java Copy Download
}
}
B. Google Cloud Platform (GCP) & Kubernetes (GKE)
Expected Questions:
1. How would you deploy a Spring Boot app on GKE?
Use Docker + Kubernetes manifests.
Example Dockerfile :
FROM openjdk:17-jdk-slim
COPY target/pharmacy-service.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
Example deployment.yaml :
apiVersion: apps/v1
kind: Deployment
metadata:
name: pharmacy-service
spec:
replicas: 3
selector:
matchLabels:
app: pharmacy
template:
spec:
dockerfile Copy Download
yaml Copy Download
containers:
- name: pharmacy
image: gcr.io/cvs-project/pharmacy-service:latest
ports:
- containerPort: 8080
2. How do you manage secrets in GCP?
Use Secret Manager or Kubernetes Secrets.
Example (GCP Secret Manager):
import *;
String secret = SecretManagerServiceClient
.getInstance()
.accessSecretVersion("projects/cvs-project/secrets/db-password/versions/latest")
.getPayload()
.getData()
.toStringUtf8();
3. How would you scale a Cloud Spanner database for a high-traffic pharmacy system?
Use instance sizing + partitioning.
Example schema optimization:
CREATE TABLE Prescriptions (
PatientId STRING(MAX) NOT NULL,
PrescriptionId STRING(MAX) NOT NULL,
Medication STRING(MAX),
-- Partition key for even distribution
com.google.cloud.secretmanager.v1.
java Copy Download
sql Copy Download
PRIMARY KEY (PatientId, PrescriptionId)
) INTERLEAVE IN PARENT Patients;
C. Kafka & Event-Driven Architecture
Expected Questions:
1. How would you design a Kafka-based prescription processing system?
Use idempotent producers + consumer groups.
Example (Spring Kafka):
@KafkaListener(topics = "new-prescriptions", groupId = "pharmacy-group")
public void processPrescription(ConsumerRecord<String, Prescription> record) {
if (isDuplicate(record.key())) return; // Idempotency check
pharmacyService.process(record.value());
}
2. How do you handle Kafka consumer lag in a pharmacy system?
Scale consumers, optimize batch processing, and monitor lag.
Example (Consumer config):
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>
();
factory.getContainerProperties().setIdleBetweenPolls(1000);
factory.setConcurrency(5); // Scale consumers
java Copy Download
java Copy Download
return factory;
}
D. CI/CD (GitHub Actions + Argo CD)
Expected Questions:
1. How would you automate deployments using GitHub Actions?
Example workflow ( .github/workflows/deploy.yml ):
name: Deploy to GKE
on: [push]
jobs:
deploy:
runs-on: ubuntu-latest
steps:
- uses: actions/checkout@v2
- run: mvn package
- uses: google-github-actions/setup-gcloud@v0
- run: gcloud container clusters get-credentials cvs-cluster --region us-east1
- run: kubectl apply -f k8s/deployment.yaml
2. How do you implement GitOps with Argo CD?
Sync Kubernetes manifests from a Git repo.
Example Application CRD:
yaml Copy Download
yaml Copy Download
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
name: pharmacy-app
spec:
destination:
namespace: pharmacy
server: https://kubernetes.default.svc
source:
repoURL: https://github.com/cvs/pharmacy-manifests.git
path: k8s
2. System Design & Behavioral Questions
A. System Design: Pharmacy Order Processing
Scenario:
Design a system to handle 10,000 pharmacy orders per second with 99.99% uptime.
Solution Approach:
1. Load Balancing: GKE autoscaling + Cloud Load Balancer.
2. Database: Cloud Spanner (global consistency).
3. Event-Driven: Kafka for order processing.
4. Caching: Memorystore (Redis) for drug inventory.
5. Monitoring: Cloud Monitoring + Prometheus.
B. Behavioral Questions
1. "Describe a time you modernized a legacy system."
Example Answer:
"At [Company], I migrated a monolithic prescription system to Spring Boot microservices on GKE, reducing latency
by 40%."
2. "How do you mentor junior engineers?"
Example Answer:
"I conduct code reviews focusing on cloud security best practices and pair programming for Kafka event modeling."
3. Final Tips
✅ Practice Coding: Focus on Java concurrency, Spring Boot, and GCP APIs.
✅ Review Case Studies: CVS likely uses GCP, Kafka, and microservices.
✅ Prepare Questions: Ask about their biggest tech debt challenges.
 */
