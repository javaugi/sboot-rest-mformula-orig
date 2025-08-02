/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

/**
 *
 * @author javau
 */
public class AzureCosmosDBDataModelAnalytics {
    
    public void step01ActivePresByLoc() {
        /*
        SELECT p.pharmacy.location, COUNT(1) AS activeCount
FROM prescriptions p
WHERE p.status = "active"
GROUP BY p.pharmacy.location
        */
    }
    
    public void step02Top5PrescribedMeds(){
/*
        SELECT TOP 5 p.medication.name, COUNT(1) AS totalPrescribed
FROM prescriptions p
GROUP BY p.medication.name
ORDER BY totalPrescribed DESC
        */        
    }
    
    public void step03AvgFulfillmentTime(){
        /*
SELECT 
  AVG(DATEDIFF("minute", p.issuedDate, p.filledDate)) AS avgFulfillmentMinutes
FROM prescriptions p
WHERE p.status = "filled"        
        */
    }
    
    public void step04PrescPerDoctor() {
        /*
SELECT p.doctorId, COUNT(1) AS totalPrescriptions
FROM prescriptions p
GROUP BY p.doctorId        
        */
    }
    
    
    public void step05ActiveOpioidPres() {
        /*
        SELECT * FROM c 
WHERE c.drugClass = "opioid" 
AND c.status = "active"
        */
    }
    
    public void step06HighRiskDrugInteractions() {
        /*
        SELECT p.patientId, p.drugName, d.interactions
FROM Prescriptions p
JOIN DrugInventory d ON p.drugName = d.drugName
WHERE ARRAY_CONTAINS(d.interactions, "benzodiazepine", true)
        */
    }
    
    public void step07PresrateByState() {
        /*
        SELECT p.address.state, COUNT(1) as prescriptions, 
       SUM(CASE WHEN r.refillsRemaining > 0 THEN 1 ELSE 0 END) as active_refills
FROM Prescriptions r
JOIN Patients p ON r.patientId = p.patientId
GROUP BY p.address.state
        */
    }
    
    public void step08DrugInvTurnoverAnalysis() {
        /*
        SELECT d.drugClass, 
       SUM(d.currentStock) as total_stock,
       COUNT(r.prescriptionId) as monthly_prescriptions
FROM DrugInventory d
LEFT JOIN (
    SELECT * FROM Prescriptions r 
    WHERE DateTimeToTimestamp(r.lastFillDate) >= DateTimeToTimestamp("2024-05-01")
) r ON d.drugName = r.drugName
GROUP BY d.drugClass
        */
    }
    
    public void step09AllPendingPresc() {
        /*
        SELECT VALUE COUNT(1)
FROM c
WHERE c.type = "Prescription"
AND c.status = "Pending"
        */
    }
    
    public void step090PrescFilledBySpecificPharmLastMonth() {
        /*
        SELECT c.prescriptionId, c.patientId, c.orderDate, c.medications[0].drugName AS mainDrug, c.status
FROM c
WHERE c.type = "Prescription"
AND c.pharmacyId = "PHARM-001"
AND c.fillDate >= "2025-06-01T00:00:00Z"
AND c.fillDate < "2025-07-01T00:00:00Z"
ORDER BY c.fillDate DESC
        */
    }
    
    public void step091AvgCoPayByPharm() {
        /*
        SELECT VALUE AVG(c.insuranceInfo.coPayAmount)
FROM c
WHERE c.type = "Prescription"
AND c.pharmacyId = "PHARM-001"
AND c.status = "Filled"
        */
    }
    
    public void step092PatientsWithMoreThanOneFilledAmoxicillin() {
        /*
        SELECT DISTINCT c.patientId
FROM c
JOIN m IN c.medications
WHERE c.type = "Prescription"
AND c.status = "Filled"
AND m.drugName = "Amoxicillin 250mg"
GROUP BY c.patientId
HAVING COUNT(c.prescriptionId) > 1
        */
    }
    
    public void step093MostRecentPrescByPatient(){
        /*
        SELECT TOP 1 *
FROM c
WHERE c.type = "Prescription"
AND c.patientId = "PAT-001"
ORDER BY c.orderDate DESC
        */
    }
    
    /*
    what does "from c" mean for the query SELECT TOP 1 *FROM cWHERE c.type = "Prescription"AND c.patientId = "PAT-001"ORDER BY c.orderDate DESC


In the query SELECT TOP 1 * FROM c WHERE c.type = "Prescription" AND c.patientId = "PAT-001" ORDER BY c.orderDate DESC, the FROM c clause indicates that you are querying a collection or container named c.

This syntax is commonly seen in NoSQL databases, specifically Azure Cosmos DB, where c is often used as an alias for the current document or item within a collection. So, FROM c means "from the collection that this query is running against, where c refers to each item in that collection."







There are patient container and prescription container etc does "From c" always work?


No, "FROM c" does not always work in all database contexts.

The use of FROM c as an alias for a collection or container is very specific to certain NoSQL databases, most notably Azure Cosmos DB (especially with its SQL API).

Here's why it's not universally applicable:

Relational Databases (SQL Server, MySQL, PostgreSQL, Oracle, etc.): In traditional relational databases, you explicitly specify the table you're querying. For example, if you had a Patients table, you would write SELECT * FROM Patients; or SELECT * FROM Patients AS p; where p is an alias you define. FROM c would likely result in an error unless you had a table literally named c.

Other NoSQL Databases: While many NoSQL databases have concepts of collections or documents, the exact syntax for querying them varies.

MongoDB: Uses db.collection.find() methods in its shell/drivers, not SQL-like FROM clauses.

Cassandra: Uses CQL (Cassandra Query Language) which is SQL-like, but you'd specify a table name, e.g., SELECT * FROM prescriptions;.

DynamoDB: Uses a proprietary API, not SQL.

In summary:

If you are working with Azure Cosmos DB's SQL API, FROM c is standard practice and essentially means "from the current container."

If you are working with almost any other database system (relational or NoSQL), FROM c will likely not work as intended and you would need to use the specific syntax for that database to reference your collections or tables.
    */
    
    
    public void step1DataModelDesignPresColl() {
        /*
        1. Prescription Data Model Design (Cosmos DB Core SQL API)
        🔷 Collection: prescriptions
        {
          "id": "RX001",
          "patientId": "P1001",
          "doctorId": "D5001",
          "medication": {
            "name": "Amoxicillin",
            "dosage": "500mg",
            "frequency": "3x daily",
            "duration_days": 10
          },
          "status": "active", // active, filled, cancelled
          "issuedDate": "2024-06-15T10:00:00Z",
          "filledDate": null,
          "pharmacy": {
            "id": "PH123",
            "location": "Austin, TX"
          },
          "notes": "Patient allergic to penicillin"
        }
        📌 Partition key: /patientId or /pharmacy/id depending on query pattern.
        */
        
    }
    
    public void step2InsertfakeData() {
        /*
2. Insert Fake Data
You can use this in the Azure Portal Data Explorer → New Item, or programmatically with SDKs.

json
Copy
Edit
{
  "id": "RX002",
  "patientId": "P1002",
  "doctorId": "D5002",
  "medication": {
    "name": "Lisinopril",
    "dosage": "10mg",
    "frequency": "1x daily",
    "duration_days": 30
  },
  "status": "filled",
  "issuedDate": "2024-06-10T09:00:00Z",
  "filledDate": "2024-06-11T16:45:00Z",
  "pharmacy": {
    "id": "PH456",
    "location": "New York, NY"
  },
  "notes": ""
}
Repeat with various values for status, medication, locations, and dates.
        
        */
    }
    
    public void step3Top4AnalyticalQueriesUsingCosmosDBSQL() {
       /*
3. Analytical Queries Using Cosmos DB SQL
🔍 1. Active Prescriptions by Location
sql
Copy
Edit
SELECT p.pharmacy.location, COUNT(1) AS activeCount
FROM prescriptions p
WHERE p.status = "active"
GROUP BY p.pharmacy.location
🔍 2. Top 5 Most Prescribed Medications
sql
Copy
Edit
SELECT TOP 5 p.medication.name, COUNT(1) AS totalPrescribed
FROM prescriptions p
GROUP BY p.medication.name
ORDER BY totalPrescribed DESC
🔍 3. Average Fulfillment Time
sql
Copy
Edit
SELECT 
  AVG(DATEDIFF("minute", p.issuedDate, p.filledDate)) AS avgFulfillmentMinutes
FROM prescriptions p
WHERE p.status = "filled"
🔍 4. Prescriptions Per Doctor
sql
Copy
Edit
SELECT p.doctorId, COUNT(1) AS totalPrescriptions
FROM prescriptions p
GROUP BY p.doctorId
⚙️ 4. Impact and Optimization Analysis
🧠 Performance Considerations:
Factor	Explanation
Partition Key	Choose based on access pattern: use /pharmacy/id if filtering/grouping by pharmacy
Composite Indexes	Add on medication.name, status, or issuedDate if frequently filtered/sorted
Query Complexity	Use TOP, SELECT value, or project to reduce I/O
Point Reads	Use id + partition key for efficient single document access

📈 Example Composite Index
json
Copy
Edit
{
  "path": "/medication/name",
  "order": "ascending"
},
{
  "path": "/issuedDate",
  "order": "descending"
}
💡 Suggestions:
Add TTL (_ts) to remove old prescriptions

Normalize large notes field if not queried often

Use Azure Synapse Link for heavy analytics

 5. Optional: Cosmos Emulator + Java Client
Would you like a ready Java project to:

Insert sample prescriptions

Run analytical queries

Monitor RU consumption

Let me know and I’ll provide a Maven-based Spring Boot starter using Cosmos DB Java SDK.
        
        */ 
    }
    
    
    public void step4ExFromGemini() {
/*
Let's design some prescription-related tables, populate them with fake data, and then perform some analytical and optimization analysis using Cosmos DB's SQL API query language.

Given the current date, I'll use dates around 2024 and 2025 for the fake data.

Data Model Design for Cosmos DB
For a NoSQL document database like Cosmos DB, you typically denormalize data and think about access patterns. Instead of "tables," we think of "containers." A single container can hold different types of documents if their access patterns are similar.

For a prescription system, we might consider the following entities and how they relate:

Prescription Order: The main record for a prescription.

Patient: Patient demographics.

Medication: Drug details.

Pharmacy: Store details.

Key Design Principles for Cosmos DB:

Partition Key: The most crucial decision. It determines how your data is distributed and affects query performance and cost. A good partition key distributes data evenly and supports common query patterns.

Item ID (id): Unique identifier within a logical partition.

Indexing: Cosmos DB automatically indexes all properties by default, but you can customize this for performance and cost.

Let's assume a primary access pattern where we frequently retrieve prescriptions for a specific patient or within a specific pharmacy.

Container: Prescriptions

Purpose: Store individual prescription orders and related details.

Partition Key: /patientId (or /pharmacyId if pharmacy-centric queries are more frequent, or a composite key like /patientId if patient-specific queries are predominant but you also query across patients for analytics). For this example, let's go with /patientId for typical patient-centric views.

Schema (example fields):

JSON

{
  "id": "unique-prescription-id-GUID",
  "type": "Prescription", // Helps distinguish document types in same container
  "prescriptionId": "P00123456", // Business ID for the prescription
  "patientId": "PAT-7890",      // Partition Key
  "pharmacyId": "PHARM-101",
  "prescriberId": "DOC-5678",
  "orderDate": "2025-06-15T10:00:00Z", // ISO 8601 UTC
  "fillDate": "2025-06-15T11:30:00Z",
  "dueDate": "2025-06-22T00:00:00Z",
  "status": "Filled", // Pending, Filled, Cancelled, Refill_Requested
  "medications": [
    {
      "drugCode": "NDC12345",
      "drugName": "Amoxicillin 250mg",
      "quantity": 30,
      "dosageInstructions": "Take one capsule by mouth every 8 hours."
    }
  ],
  "insuranceInfo": {
    "provider": "BlueCross",
    "policyNumber": "ABC123XYZ",
    "coPayAmount": 10.50
  },
  "refillsRemaining": 2,
  "lastRefillDate": "2025-05-15T10:00:00Z",
  "creationTimestamp": "2025-06-15T10:00:00Z", // For audit/TTL
  "lastUpdatedTimestamp": "2025-06-15T11:30:00Z"
}
Container: Patients

Purpose: Store patient demographic information.

Partition Key: /id (simple unique ID for each patient).

Schema:

JSON

{
  "id": "PAT-7890", // Partition Key
  "type": "Patient",
  "firstName": "Alice",
  "lastName": "Smith",
  "dateOfBirth": "1980-01-15",
  "gender": "Female",
  "contact": {
    "email": "alice.smith@example.com",
    "phone": "555-123-4567",
    "address": {
      "street": "123 Main St",
      "city": "Anytown",
      "state": "GA",
      "zip": "30303"
    }
  },
  "allergies": ["Penicillin", "Sulfa"],
  "medConditions": ["Hypertension"],
  "registrationDate": "2020-03-01T08:00:00Z"
}
Container: Pharmacies

Purpose: Store pharmacy location and details.

Partition Key: /id (simple unique ID for each pharmacy).

Schema:

JSON

{
  "id": "PHARM-101", // Partition Key
  "type": "Pharmacy",
  "name": "CVS Pharmacy #101",
  "address": {
    "street": "456 Oak Ave",
    "city": "Anytown",
    "state": "GA",
    "zip": "30303"
  },
  "phone": "555-987-6543",
  "operatingHours": {
    "mon-fri": "09:00-21:00",
    "sat": "09:00-18:00",
    "sun": "Closed"
  },
  "latitude": 33.748997,
  "longitude": -84.387982
}
Populating Fake Data (Cosmos DB Data Explorer or SDK)
You can use the Azure Portal's Data Explorer, or an SDK (Java, .NET, Python, Node.js) to insert these documents. For brevity, I'll show the JSON documents.

Assumptions:

Cosmos DB Account: cvspharmacycosmosdb (replace with your actual name)

Database: PharmacyDB

Containers: Prescriptions, Patients, Pharmacies

1. Patients Container Data

JSON

[
  {
    "id": "PAT-001",
    "type": "Patient",
    "firstName": "Alice",
    "lastName": "Smith",
    "dateOfBirth": "1980-01-15",
    "gender": "Female",
    "contact": { "email": "alice.smith@example.com", "phone": "555-111-2222" },
    "allergies": ["Penicillin"],
    "medConditions": ["Hypertension"],
    "registrationDate": "2024-01-10T09:00:00Z"
  },
  {
    "id": "PAT-002",
    "type": "Patient",
    "firstName": "Bob",
    "lastName": "Johnson",
    "dateOfBirth": "1975-05-20",
    "gender": "Male",
    "contact": { "email": "bob.johnson@example.com", "phone": "555-333-4444" },
    "allergies": [],
    "medConditions": ["Diabetes"],
    "registrationDate": "2023-11-20T14:30:00Z"
  },
  {
    "id": "PAT-003",
    "type": "Patient",
    "firstName": "Carol",
    "lastName": "Davis",
    "dateOfBirth": "1992-09-01",
    "gender": "Female",
    "contact": { "email": "carol.davis@example.com", "phone": "555-555-6666" },
    "allergies": ["Sulfa"],
    "medConditions": [],
    "registrationDate": "2025-01-05T11:00:00Z"
  }
]
2. Pharmacies Container Data

JSON

[
  {
    "id": "PHARM-001",
    "type": "Pharmacy",
    "name": "CVS Pharmacy Midtown",
    "address": { "street": "123 Peachtree St NE", "city": "Atlanta", "state": "GA", "zip": "30308" },
    "phone": "404-111-1111"
  },
  {
    "id": "PHARM-002",
    "type": "Pharmacy",
    "name": "CVS Pharmacy Downtown",
    "address": { "street": "456 Main St SW", "city": "Atlanta", "state": "GA", "zip": "30303" },
    "phone": "404-222-2222"
  }
]
3. Prescriptions Container Data

To demonstrate queries, I'll create a mix of statuses, medication types, and dates.

JSON

[
  {
    "id": "PRES-001",
    "type": "Prescription",
    "prescriptionId": "P1001",
    "patientId": "PAT-001",
    "pharmacyId": "PHARM-001",
    "prescriberId": "DOC-A",
    "orderDate": "2025-06-01T10:00:00Z",
    "fillDate": "2025-06-01T11:00:00Z",
    "dueDate": "2025-06-08T00:00:00Z",
    "status": "Filled",
    "medications": [
      { "drugCode": "DRUG001", "drugName": "Amoxicillin 250mg", "quantity": 30, "dosageInstructions": "Once daily" }
    ],
    "insuranceInfo": { "provider": "BlueCross", "coPayAmount": 10.00 },
    "refillsRemaining": 1,
    "lastRefillDate": null
  },
  {
    "id": "PRES-002",
    "type": "Prescription",
    "prescriptionId": "P1002",
    "patientId": "PAT-001",
    "pharmacyId": "PHARM-001",
    "prescriberId": "DOC-B",
    "orderDate": "2025-06-10T14:00:00Z",
    "fillDate": null,
    "dueDate": "2025-06-17T00:00:00Z",
    "status": "Pending",
    "medications": [
      { "drugCode": "DRUG002", "drugName": "Metformin 500mg", "quantity": 60, "dosageInstructions": "Twice daily" }
    ],
    "insuranceInfo": { "provider": "BlueCross", "coPayAmount": 5.00 },
    "refillsRemaining": 3,
    "lastRefillDate": null
  },
  {
    "id": "PRES-003",
    "type": "Prescription",
    "prescriptionId": "P1003",
    "patientId": "PAT-002",
    "pharmacyId": "PHARM-002",
    "prescriberId": "DOC-C",
    "orderDate": "2025-05-25T09:30:00Z",
    "fillDate": "2025-05-25T10:00:00Z",
    "dueDate": "2025-06-01T00:00:00Z",
    "status": "Filled",
    "medications": [
      { "drugCode": "DRUG003", "drugName": "Lisinopril 10mg", "quantity": 30, "dosageInstructions": "Once daily" }
    ],
    "insuranceInfo": { "provider": "Aetna", "coPayAmount": 15.00 },
    "refillsRemaining": 0,
    "lastRefillDate": "2025-04-25T09:00:00Z"
  },
  {
    "id": "PRES-004",
    "type": "Prescription",
    "prescriptionId": "P1004",
    "patientId": "PAT-002",
    "pharmacyId": "PHARM-002",
    "prescriberId": "DOC-C",
    "orderDate": "2025-06-12T16:00:00Z",
    "fillDate": null,
    "dueDate": "2025-06-19T00:00:00Z",
    "status": "Cancelled",
    "medications": [
      { "drugCode": "DRUG004", "drugName": "Adderall XR 20mg", "quantity": 30, "dosageInstructions": "Once daily" }
    ],
    "insuranceInfo": { "provider": "Aetna", "coPayAmount": 20.00 },
    "refillsRemaining": 0,
    "lastRefillDate": null
  },
  {
    "id": "PRES-005",
    "type": "Prescription",
    "prescriptionId": "P1005",
    "patientId": "PAT-003",
    "pharmacyId": "PHARM-001",
    "prescriberId": "DOC-A",
    "orderDate": "2025-06-15T08:00:00Z",
    "fillDate": "2025-06-15T08:45:00Z",
    "dueDate": "2025-06-22T00:00:00Z",
    "status": "Filled",
    "medications": [
      { "drugCode": "DRUG001", "drugName": "Amoxicillin 250mg", "quantity": 20, "dosageInstructions": "Once daily" },
      { "drugCode": "DRUG005", "drugName": "Zyrtec 10mg", "quantity": 90, "dosageInstructions": "Once daily" }
    ],
    "insuranceInfo": { "provider": "Cigna", "coPayAmount": 8.00 },
    "refillsRemaining": 2,
    "lastRefillDate": null
  },
  {
    "id": "PRES-006",
    "type": "Prescription",
    "prescriptionId": "P1006",
    "patientId": "PAT-001",
    "pharmacyId": "PHARM-002",
    "prescriberId": "DOC-B",
    "orderDate": "2025-04-01T09:00:00Z",
    "fillDate": "2025-04-01T10:00:00Z",
    "dueDate": "2025-04-08T00:00:00Z",
    "status": "Filled",
    "medications": [
      { "drugCode": "DRUG006", "drugName": "Atorvastatin 40mg", "quantity": 30, "dosageInstructions": "Once nightly" }
    ],
    "insuranceInfo": { "provider": "BlueCross", "coPayAmount": 7.50 },
    "refillsRemaining": 0,
    "lastRefillDate": "2025-03-01T10:00:00Z"
  }
]
Analytical Impact & Optimization Analysis with Cosmos DB Queries
Now, let's write some Cosmos DB SQL API queries and analyze their impact and optimization potential.

Basic Setup Note: When running these queries in Azure Portal's Data Explorer, make sure you select the correct container (e.g., Prescriptions, Patients, Pharmacies) for the c alias to refer to the documents in that container.

Query 1: Retrieve all filled prescriptions for a specific patient.
Query:

SQL

SELECT *
FROM c
WHERE c.type = "Prescription"
AND c.patientId = "PAT-001"
AND c.status = "Filled"
Analytical Impact:

Business Value: Allows pharmacists/patients to quickly see past filled prescriptions, useful for medication history, adherence checks.

Operational: Supports core application functionality for patient portals or pharmacy systems.

Optimization Analysis:

Partition Key Usage: Highly optimized! This query filters directly on patientId, which is the partition key for the Prescriptions container. Cosmos DB can route this query directly to the relevant logical partition, making it a "single-partition query."

Indexing: type and status are also used in the filter. By default, Cosmos DB indexes all properties, so these predicates are efficiently used.

Cost (RUs): Very low RUs for typical data volumes because it's a single-partition query and leverages indexes efficiently.

Improvement: None needed for this specific query pattern, as it's already using the partition key.

Query 2: Count total pending prescriptions across all pharmacies.
Query:

SQL

SELECT VALUE COUNT(1)
FROM c
WHERE c.type = "Prescription"
AND c.status = "Pending"
Analytical Impact:

Business Value: Provides an overview of current workload, helps in resource allocation (e.g., how many pharmacists are needed).

Operational: Dashboard metric for pharmacy operations.

Optimization Analysis:

Partition Key Usage: Cross-partition query. This query does not specify a patientId. Cosmos DB must fan out the query to all logical partitions to gather results, then aggregate them.

Indexing: The status property is indexed, which helps filter documents quickly within each partition.

Cost (RUs): Higher RUs than single-partition queries. The cost scales with the number of logical partitions scanned. For a large number of patients, this can be significant.

Improvement:

Change Partition Key: If this query (and other cross-partition queries) is very frequent and performance-critical, you might consider a different partition key (e.g., a synthetic key like /status if statuses are few, or denormalization). However, changing the partition key impacts all queries.

Materialized View/Aggregation Layer: For analytical queries that are run frequently or need to be very cheap, consider:

Azure Synapse Link for Azure Cosmos DB: For near real-time analytics without impacting transactional workloads, creating a materialized view in Synapse Analytics.

Azure Stream Analytics: Process changes feed from Cosmos DB to an aggregate store (e.g., another Cosmos DB container with a different partition key, or Azure SQL DB) that is optimized for this query.

Pre-aggregation: For very high-volume scenarios, you might pre-aggregate counts and store them in a separate document that is updated periodically.

Query 3: Find prescriptions filled at a specific pharmacy in the last month.
Query:

SQL

SELECT c.prescriptionId, c.patientId, c.orderDate, c.medications[0].drugName AS mainDrug, c.status
FROM c
WHERE c.type = "Prescription"
AND c.pharmacyId = "PHARM-001"
AND c.fillDate >= "2025-06-01T00:00:00Z"
AND c.fillDate < "2025-07-01T00:00:00Z"
ORDER BY c.fillDate DESC
Analytical Impact:

Business Value: Helps pharmacy managers track volume, identify busy periods, and analyze drug dispensing trends per location.

Operational: Reporting, auditing.

Optimization Analysis:

Partition Key Usage: Cross-partition query. Similar to Query 2, pharmacyId is not the partition key, so this is a fan-out query.

Indexing: pharmacyId, fillDate, and type are indexed, making the filters efficient within each partition. ORDER BY c.fillDate will benefit from a composite index on fillDate if one is defined, otherwise it's an in-memory sort which can be expensive for large result sets.

Cost (RUs): Moderate to high RUs depending on the number of partitions.

Improvement:

Composite Index: If fillDate sorting is critical, define a composite index on (fillDate ASC).

Denormalization/Separate Container: If pharmacy-specific queries are frequent, you might denormalize data into a PharmacyPrescriptions container with /pharmacyId as the partition key. This means duplicating prescription data, which adds write cost and consistency challenges.

Synapse Link / Batch Processing: For historical analysis over large date ranges, Synapse Link or batch processing (e.g., Azure Data Factory moving data to a data warehouse) is more suitable.

Query 4: Calculate the average co-pay amount for filled prescriptions in a specific pharmacy.
Query:

SQL

SELECT VALUE AVG(c.insuranceInfo.coPayAmount)
FROM c
WHERE c.type = "Prescription"
AND c.pharmacyId = "PHARM-001"
AND c.status = "Filled"
Analytical Impact:

Business Value: Financial analysis, revenue projection per pharmacy, insights into insurance agreements.

Optimization Analysis:

Partition Key Usage: Cross-partition query. Again, pharmacyId is not the partition key.

Indexing: pharmacyId, status, insuranceInfo.coPayAmount are used. Default indexing works well.

Cost (RUs): Similar to Query 3, scales with partitions.

Improvement: Same as Query 3. For aggregate queries across partitions, often a separate analytical store or a pre-aggregation step is more efficient for very large datasets.

Query 5: Find patients with more than one filled prescription of "Amoxicillin".
Query (Using JOIN for array iteration):

SQL

SELECT DISTINCT c.patientId
FROM c
JOIN m IN c.medications
WHERE c.type = "Prescription"
AND c.status = "Filled"
AND m.drugName = "Amoxicillin 250mg"
GROUP BY c.patientId
HAVING COUNT(c.prescriptionId) > 1
Analytical Impact:

Business Value: Identify patients frequently taking certain medications, useful for adherence programs, targeted health campaigns, or identifying potential polypharmacy issues.

Optimization Analysis:

Partition Key Usage: Cross-partition query. This query operates across all partitions to find patients.

Indexing: status, medications array (specifically drugName within it) are used. Cosmos DB's automatic indexing includes array elements.

Cost (RUs): This query involves JOIN (self-join on the medications array), GROUP BY, and HAVING, which are typically more expensive operations in Cosmos DB, especially on large datasets. The RUs will be significantly higher than simple SELECT or COUNT queries.

Improvement:

Denormalization: If this pattern is very common, you might consider denormalizing drug usage onto the Patient document (e.g., patient.medicationHistory: [{drugName: "Amoxicillin", lastFillDate: "...", totalFills: 5}]). This simplifies patient-centric queries but requires careful updates.

Pre-computation: If this is a reporting metric, a daily/weekly batch job could pre-compute this and store the results in a separate analytics store or a summary document.

Spark/Synapse Link: For complex analytical queries involving multiple aggregations or joins, a Big Data analytics platform (like Azure Databricks with Spark or Azure Synapse Analytics with Synapse Link) is far more suitable and cost-effective than direct Cosmos DB queries.

Query 6: Retrieve patient details along with their most recent prescription. (Conceptual, requires client-side join or highly denormalized data)
Problem with direct join in Cosmos DB: Cosmos DB SQL API does not support joins across different containers directly within a single query. You must join in your application code.

Approach 1: Application-side Join (most common)

Query Patients container for PAT-001.

Query Prescriptions container for PAT-001 (using partition key /patientId) and order by orderDate DESC with TOP 1.

Combine results in your application.

Query (for Prescriptions to get most recent for PAT-001):

SQL

SELECT TOP 1 *
FROM c
WHERE c.type = "Prescription"
AND c.patientId = "PAT-001"
ORDER BY c.orderDate DESC
Optimization Analysis (for the Prescription part):

Partition Key Usage: Highly optimized single-partition query.

Indexing: orderDate should be indexed for efficient ORDER BY.

Cost: Very low RUs.

Approach 2: Denormalization (if access pattern justifies)

If "patient with most recent prescription" is an extremely frequent read operation, you might denormalize the most recent prescription details directly onto the Patient document. This means updating the Patient document every time a new prescription is added/filled.

Patient Document (Denormalized Example):

JSON

{
  "id": "PAT-001",
  "type": "Patient",
  // ... other patient details ...
  "mostRecentPrescription": {
    "prescriptionId": "P1002",
    "orderDate": "2025-06-10T14:00:00Z",
    "status": "Pending",
    "drugName": "Metformin 500mg"
  }
}
Optimization Analysis (Denormalized Approach):

Read Cost: Extremely low (single read from Patients container).

Write Cost: Higher, as you incur an additional write operation (update Patient document) every time a prescription is created/updated.

Consistency: Requires careful handling of eventual consistency and potential conflicts if multiple updates occur concurrently.

Conclusion on Optimization
Single-Partition Queries (Point Reads): These are the most efficient and cost-effective queries in Cosmos DB. Design your partition key to support your most frequent, performance-critical access patterns.

Cross-Partition Queries: These are more expensive and should be minimized for high-volume operational workloads.

Strategies to mitigate:

Change Partition Key: If primary access patterns shift.

Index Optimization: Ensure relevant properties are indexed (default usually good, but check for compound indexes or exclusion paths).

Denormalization: Duplicate data for specific read patterns, but manage consistency.

Materialized Views/Pre-aggregation: For analytical needs, push aggregation to a separate process or analytical store (Synapse Link, Stream Analytics, Spark).

Complex Queries (JOIN, GROUP BY, OFFSET/LIMIT): These can be very expensive in terms of RUs and latency on large datasets in Cosmos DB's transactional store. For true analytical insights, offload these to dedicated analytical platforms.

By understanding these principles and analyzing the RU cost of your queries, you can design a highly optimized and cost-effective solution using Cosmos DB.        
        */        
    }
    
    public void step5ExFromDeepSeek() {
/*
Here's a comprehensive guide to designing prescription-related tables in Cosmos DB, populating fake data, and performing analytical queries with optimization insights:

1. Cosmos DB Data Model Design
Tables (Containers) & Relationships
Container	Partition Key	Description
Patients	/patientId	Patient demographics
Prescriptions	/patientId	Active medications
DrugInventory	/drugClass	Pharmacy stock levels
Physicians	/npiNumber	Prescribing doctors
2. Sample Data Population (SQL API)
2.1 Patients Container
sql
-- Insert sample patients
SELECT * FROM c WHERE c.patientId IN ("PAT001", "PAT002")
Sample Document:

json
{
  "patientId": "PAT001",
  "name": "John Doe",
  "dob": "1985-04-12",
  "allergies": ["penicillin", "sulfa"],
  "address": {
    "state": "NY",
    "zip": "10001"
  }
}
2.2 Prescriptions Container
sql
-- Get all active opioid prescriptions
SELECT * FROM c 
WHERE c.drugClass = "opioid" 
AND c.status = "active"
Sample Document:

json
{
  "prescriptionId": "RX-2024-0567",
  "patientId": "PAT001",
  "drugName": "Oxycodone",
  "drugClass": "opioid",
  "dosage": "10mg",
  "refillsRemaining": 2,
  "physicianNPI": "1234567890",
  "lastFillDate": "2024-05-15"
}
3. Analytical Queries with Optimization
3.1 High-Risk Drug Interactions
sql
SELECT p.patientId, p.drugName, d.interactions
FROM Prescriptions p
JOIN DrugInventory d ON p.drugName = d.drugName
WHERE ARRAY_CONTAINS(d.interactions, "benzodiazepine", true)
Optimization:

Create a composite index on (/drugName, /interactions)

Use ARRAY_CONTAINS with true for exact matches

3.2 Prescription Fulfillment Rates by State
sql
SELECT p.address.state, COUNT(1) as prescriptions, 
       SUM(CASE WHEN r.refillsRemaining > 0 THEN 1 ELSE 0 END) as active_refills
FROM Prescriptions r
JOIN Patients p ON r.patientId = p.patientId
GROUP BY p.address.state
Optimization:

Cross-partition query (enable EnableCrossPartitionQuery)

Materialized views for state-level aggregates

3.3 Drug Inventory Turnover Analysis
sql
SELECT d.drugClass, 
       SUM(d.currentStock) as total_stock,
       COUNT(r.prescriptionId) as monthly_prescriptions
FROM DrugInventory d
LEFT JOIN (
    SELECT * FROM Prescriptions r 
    WHERE DateTimeToTimestamp(r.lastFillDate) >= DateTimeToTimestamp("2024-05-01")
) r ON d.drugName = r.drugName
GROUP BY d.drugClass
Optimization:

Use DateTimeToTimestamp for date comparisons

Pre-aggregate monthly data with Change Feed

4. Performance Impact Analysis
Query	RU Cost (Before)	RU Cost (After)	Optimization Technique
Drug Interactions	78 RUs	32 RUs	Composite index on drugName
State Fulfillment	145 RUs	62 RUs	Materialized view pre-aggregation
Inventory Turnover	210 RUs	89 RUs	Change Feed processor
5. Indexing Policy (Add to Container Settings)
json
{
  "indexingMode": "consistent",
  "automatic": true,
  "includedPaths": [
    { "path": "/patientId/?" },
    { "path": "/drugName/?" },
    { "path": "/drugClass/?" }
  ],
  "compositeIndexes": [
    [ { "path": "/drugName", "order": "ascending" }, 
      { "path": "/interactions", "order": "ascending" } ]
  ]
}
6. Data Generator Script (Python)
python
import random, uuid
from faker import Faker
fake = Faker()

def generate_patient():
    return {
        "patientId": f"PAT{uuid.uuid4().hex[:6]}",
        "name": fake.name(),
        "dob": fake.date_of_birth().isoformat(),
        "allergies": random.choice([[], ["penicillin"], ["sulfa", "latex"]])
    }

# Generate 1000 patients
patients = [generate_patient() for _ in range(1000)]
Key Takeaways
Partitioning Strategy: Patient-centric (/patientId) for prescriptions

Query Patterns: Prioritize equality filters over range queries

Cost Control: Monitor RU/s with Azure Metrics (set alerts > 80% utilization)        
        */        
    }
}
