/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

/**
 *
 * @author javau
 */
public class TechStackSelection {
    
} 
/*
Key Differences:
Approach                    Best For                        Dependencies            Scalability
Spring Cloud Gateway        Microservices architecture      spring-cloud-gateway	High (cluster-wide)
Bucket4j standalone         Monolithic apps                 bucket4j-core           Good (needs shared cache)
Bucket4j + Redis            Distributed systems             bucket4j-redis          Excellent

For an OTA update system handling vehicle fleets, I recommend either:
    Spring Cloud Gateway if you have microservices
    Bucket4j + Redis if you need advanced control in a monolith
*/

/*
<!-- Bucket4j Redis for advanced rate limiting -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-redis</artifactId>
    <version>7.6.0</version>
</dependency>
*/