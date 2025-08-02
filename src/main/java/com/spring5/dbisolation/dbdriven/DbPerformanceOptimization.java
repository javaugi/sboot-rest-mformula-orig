/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

/*
5. How would you optimize a slow-running query in a Spring Boot application?
Detailed Description:
Query optimization involves:
    1. Analyzing execution plans and finetune sql - select with where using primary key, foreign key and id, all fields of the combo index
    2. Adding appropriate indexes
        1. Add indexes to tables and columns
        2. Add indexes to Entities
        3. Using Native query with query hints
        4. Using Spring Data JPA Projections to retrieve bulk data instead of multiple repository query
        5. Using Batch processing for insert or update
        6. Using stored procedure if needed
    3. Optimizing JOIN operations with FETCH JOIN
    4. Using filtering and pagination
    5. Caching frequently accessed data with Redis cache replications
    6. Scale DB horizontally and cache aggressively
    7. Denormalization carefully and used only if required and pay attention to data integrity with triigers setup properly
*/
public class DbPerformanceOptimization {
    
}
