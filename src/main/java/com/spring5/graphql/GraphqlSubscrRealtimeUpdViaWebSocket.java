/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

/**
 *
 * @author javau
 */
public class GraphqlSubscrRealtimeUpdViaWebSocket {
    
}
/*
could you give me an Subscriptions (real-time updates via WebSocket) example
Here's a complete example of GraphQL subscriptions in Spring Boot using WebSocket for real-time prescription updates:

1. Dependencies (pom.xml)
Add WebSocket and GraphQL subscription support:

xml
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>12.0.0</version>
</dependency>
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-java-tools</artifactId>
    <version>12.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
2. Schema Update (schema.graphqls)
Add Subscription type:

graphql
type Subscription {
    prescriptionAdded(patientId: ID!): Prescription!
}

type Prescription {
    id: ID!
    patientId: ID!
    drugName: String!
    dosage: String!
}
3. Publisher Setup (Backend Service)
Create an event publisher using Reactor:

java
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class PrescriptionPublisher {
    private final Sinks.Many<Prescription> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(Prescription prescription) {
        sink.tryEmitNext(prescription);
    }

    public Publisher<Prescription> getPublisher(String patientId) {
        return sink.asFlux()
            .filter(p -> p.getPatientId().equals(patientId));
    }
}
4. Subscription Resolver
java
@Component
public class PrescriptionSubscriptionResolver implements GraphQLSubscriptionResolver {
    @Autowired
    private PrescriptionPublisher publisher;

    public Publisher<Prescription> prescriptionAdded(String patientId) {
        return publisher.getPublisher(patientId);
    }
}
5. Trigger Events (Mutation Example)
java
@Component
public class PrescriptionMutationResolver implements GraphQLMutationResolver {
    @Autowired
    private PrescriptionRepository repo;
    @Autowired
    private PrescriptionPublisher publisher;

    public Prescription addPrescription(String patientId, String drugName) {
        Prescription newRx = repo.save(new Prescription(patientId, drugName));
        publisher.publish(newRx); // Notify subscribers
        return newRx;
    }
}
6. WebSocket Configuration
java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(graphQLWebSocketHandler(), "/subscriptions")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler graphQLWebSocketHandler() {
        return new GraphQLWebSocketServletHandler();
    }
}
7. Testing with GraphiQL
Subscription Query:

graphql
subscription {
    prescriptionAdded(patientId: "PAT001") {
        id
        drugName
    }
}
Mutation to Trigger:

graphql
mutation {
    addPrescription(patientId: "PAT001", drugName: "Ibuprofen") {
        id
    }
}
8. Expected Real-Time Flow
Client subscribes to prescriptionAdded via WebSocket (ws://yourserver/subscriptions)

When addPrescription mutation executes:

Diagram
Code
All subscribed clients receive:

json
{
    "data": {
        "prescriptionAdded": {
            "id": "RX-123",
            "drugName": "Ibuprofen"
        }
    }
}
Key Takeaways
WebSocket Endpoint: /subscriptions (configurable)

Backpressure Handling: Reactor's Flux manages slow clients

Filtering: Only sends updates for the specified patientId

Scalability: For production, consider Redis Pub/Sub for distributed systems
*/

/*
GraphQL is more effective than traditional REST with JPA repositories in several key scenarios, despite still needing to fetch data from a database. Here's why:

1. Precise Data Fetching (No Over/Under-Fetching)
Scenario	REST/JPA	GraphQL
Mobile App	GET /patient/PAT001 returns all fields (even unneeded)	Client requests only { name, prescriptions { drugName } }
Web Dashboard	Requires separate DTOs or endpoints	Same endpoint, different query fields
Database Impact:
GraphQL reduces unnecessary fields loaded from DB → Less data transferred over the network and processed by JPA.

2. Single Request for Nested Data
Problem in REST/JPA:

Diagram
Code




N+1 Query Problem: Common with JPA lazy loading.

GraphQL Solution:

graphql
query {
    patient(id: "PAT001") {
        name
        prescriptions {  # Resolved in 1 optimized DB query
            drugName
        }
    }
}
Optimized DataFetcher:

java
public DataFetcher<List<Prescription>> prescriptions() {
    return env -> {
        Patient patient = env.getSource();
        // Single query with JOIN FETCH
        return em.createQuery(
            "SELECT p FROM Prescription p JOIN FETCH p.patient WHERE p.patient.id = :pid", 
            Prescription.class)
            .setParameter("pid", patient.getId())
            .getResultList();
    };
}
3. Batching & Caching
a. Batch Loading (Avoids N+1)

java
@Autowired
private BatchLoaderRegistry registry;

registry.forTypePair(UUID.class, Patient.class)
       .registerBatchLoader(patientIds -> 
           repo.findAllById(patientIds)); // Single SQL IN query
b. Caching
GraphQL Java caches parsed queries + supports DataLoader for request-scoped caching.

4. Frontend-Driven Development
Client A (Mobile):

graphql
query {
    patient(id: "PAT001") {
        name
        riskScore
    }
}
Client B (Admin Portal):

graphql
query {
    patient(id: "PAT001") {
        name
        prescriptions {
            drugName
            interactions
        }
    }
}
Same endpoint, different needs → No backend changes required.

5. Real-Time Updates (Subscriptions)
graphql
subscription {
    prescriptionAdded(patientId: "PAT001") {
        drugName
        dosage
    }
}
WebSocket push vs REST polling → More efficient for real-time apps.

When GraphQL Doesn't Outperform JPA/REST
Simple CRUD Apps:
REST with JpaRepository is faster to implement for basic needs.

Large Binary Transfers:
GraphQL isn't optimized for file uploads/downloads.

Caching Complexity:
REST's HTTP caching (e.g., CDNs) is more straightforward.

Optimization Cheat Sheet
Technique	GraphQL Implementation	JPA/REST Equivalent
Batching	DataLoader	@BatchSize (Hibernate)
Caching	Request-scoped DataLoader	Spring Cache
Query Optimization	JOIN FETCH in DataFetcher	@EntityGraph
Projections	Client selects fields	@JsonView or DTOs
Key Takeaway
GraphQL shines when you need:
✅ Flexible client data requirements
✅ Complex/nested queries
✅ Real-time updates
✅ Microservices aggregation

For simple apps, stick with REST/JPA. For healthcare apps like your pharmacy system with varying client needs (mobile/web/analytics), GraphQL is more efficient at scale.
*/