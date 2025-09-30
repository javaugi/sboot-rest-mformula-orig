/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

/*
Q: When would you choose a NoSQL solution like MongoDB for this system?
Sample Answer:
"NoSQL could be beneficial for:
    Storing vehicle telemetry data that's high-volume and schema-less
    Caching update packages metadata that changes frequently
    Handling the eventual consistency requirements for update status reporting across regions
However, for the core update tracking system where ACID properties are crucial (like ensuring an update
    isn't sent twice), I'd stick with PostgreSQL. A hybrid approach might work best."

CI/CD Pipeline Questions
Q: How would you implement CI/CD for an OTA update backend system?
Sample Answer:
"I'd implement a robust pipeline with:
    Code Commit: Trigger automated builds on Git push with branch protection
    Build: Maven/Gradle build with unit tests, static analysis (SonarQube)
    Test: Integration tests against a staging environment with vehicle simulators
    Deploy: Canary deployments to a small percentage of production first
    Monitor: Automated rollback if error rates exceed thresholds
For OTA specifically, I'd include:
    Special validation for update package metadata
    Performance testing under simulated mass update scenarios
    Security scanning of all artifacts"
 */
public class HowToChooseDbNoSQLOrACID {
}
