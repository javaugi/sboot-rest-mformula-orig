/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
/*
1. Resolvers (Similar to Controllers) 
    - QueryResolver(GraphQLQueryResolver ): Queries(GET-like operations)
    - MutationResolver(GraphQLMutationResolver ): Mutations (POST/PUT/DELETE-like operations)
    - Subscriptions (real-time updates via WebSocket)
2. Regular JPA Repositories
3. Custom DataFetchers (For Complex Queries) - DataFetcher<List<GraphAuthor>> 

Key Differences vs REST/JPA
    Feature                 GraphQL                                 REST/JPA
    Data Fetching           Client-defined queries                  Fixed endpoints
    Overfetching            Avoided (request only needed fields)	Common (GET /patients returns all fields)
    Repository              Resolvers/DataFetchers                  JpaRepository
    Nested Data             Handled in resolvers                    Requires joins or DTOs

Tools for GraphQL in Spring Boot
    GraphiQL: Built-in UI for testing queries (/graphiql)
    Playground: Alternative UI (/playground)
    DGS Framework: Netflix's GraphQL library (alternative to graphql-java-tools)

When to Use GraphQL?
    ✅ Complex client data requirements (e.g., mobile vs web different fields)
    ✅ Microservices aggregation (single GraphQL gateway)
    ❌ Simple CRUD apps (REST/JPA is faster to implement)
*/

@Service
public class GraphAuthorService {
    private final Map<String, GraphAuthor> authors = new ConcurrentHashMap<>();

    public GraphAuthorService() {
        authors.put("A1", new GraphAuthor("A1", "Jane", "Doe"));
        authors.put("A2", new GraphAuthor("A2", "John", "Smith"));
    }

    public GraphAuthor findAuthorById(String id) {
        return authors.get(id);
    }    
}
/*
1. GraphQL in Spring Boot Basics
GraphQL is a query language for APIs that lets clients request exactly what they need. Unlike REST, it uses:

Queries (GET-like operations)

Mutations (POST/PUT/DELETE-like operations)

Subscriptions (real-time updates via WebSocket)

2. Key Components in Spring Boot
2.1 Dependencies (pom.xml)
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
2.2 GraphQL Schema (schema.graphqls)
graphql
type Patient {
    id: ID!
    name: String!
    prescriptions: [Prescription!]!
}

type Prescription {
    id: ID!
    drugName: String!
    dosage: String!
}

type Query {
    getPatient(id: ID!): Patient
    getAllPatients: [Patient!]!
}

type Mutation {
    addPrescription(patientId: ID!, drugName: String!): Prescription!
}
3. Resolvers (Similar to Controllers)
3.1 Query Resolver
java
@Component
public class PatientQueryResolver implements GraphQLQueryResolver {
    @Autowired
    private PatientRepository patientRepo;

    public Patient getPatient(String id) {
        return patientRepo.findById(id).orElseThrow();
    }
}
3.2 Mutation Resolver
java
@Component
public class PrescriptionMutationResolver implements GraphQLMutationResolver {
    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public Prescription addPrescription(String patientId, String drugName) {
        return prescriptionRepo.save(new Prescription(patientId, drugName));
    }
}
4. Is There a GraphRepository Like JpaRepository?
No, but you have alternatives:

4.1 Regular JPA Repositories
java
public interface PatientRepository extends JpaRepository<Patient, String> {
    // Standard CRUD methods work
}
4.2 Custom DataFetchers (For Complex Queries)
java
@Component
public class PrescriptionDataFetcher implements DataFetcher<List<Prescription>> {
    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Override
    public List<Prescription> get(DataFetchingEnvironment env) {
        Patient patient = env.getSource();
        return prescriptionRepo.findByPatientId(patient.getId());
    }
}
5. Key Differences vs REST/JPA
Feature	GraphQL	REST/JPA
Data Fetching	Client-defined queries	Fixed endpoints
Overfetching	Avoided (request only needed fields)	Common (GET /patients returns all fields)
Repository	Resolvers/DataFetchers	JpaRepository
Nested Data	Handled in resolvers	Requires joins or DTOs
6. Example Query & Response
Request:

graphql
query {
    getPatient(id: "PAT001") {
        name
        prescriptions {
            drugName
        }
    }
}
Response:

json
{
    "data": {
        "getPatient": {
            "name": "John Doe",
            "prescriptions": [
                { "drugName": "Oxycodone" }
            ]
        }
    }
}
7. Tools for GraphQL in Spring Boot
GraphiQL: Built-in UI for testing queries (/graphiql)

Playground: Alternative UI (/playground)

DGS Framework: Netflix's GraphQL library (alternative to graphql-java-tools)

When to Use GraphQL?
✅ Complex client data requirements (e.g., mobile vs web different fields)

✅ Microservices aggregation (single GraphQL gateway)

❌ Simple CRUD apps (REST/JPA is faster to implement)
*/