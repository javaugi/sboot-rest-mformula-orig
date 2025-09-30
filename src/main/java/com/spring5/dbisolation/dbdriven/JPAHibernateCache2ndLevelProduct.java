/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/*
Database & PostgreSQL Questions
3. Explain JPA/Hibernate caching mechanisms - Detailed Description:
Hibernate provides two levels of caching:
    First Level Cache - Session-scoped cache enabled by default
    Second Level Cache - SessionFactory-scoped cache that needs explicit configuration
    Query Cache - Caches query results
Java + PostgreSQL Code Example:
 */
@Data
@Builder(toBuilder = true)
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JPAHibernateCache2ndLevelProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
}
/* pom.xml
<!-- For Ehcache as second-level cache provider -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-ehcache</artifactId>
</dependency>
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
 */

 /*
// application.properties
# Enable second-level cache
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
# Enable query cache
spring.jpa.properties.hibernate.cache.use_query_cache=true
# Show SQL for demonstration
spring.jpa.show-sql=true
 */

 /*
Create ehcache.xml in src/main/resources:

xml
<ehcache>
    <defaultCache
        maxElementsInMemory="10000"
        eternal="false"
        timeToIdleSeconds="300"
        timeToLiveSeconds="600"
        overflowToDisk="false"
    />

    <cache name="com.example.model.Product"
           maxElementsInMemory="1000"
           eternal="false"
           timeToIdleSeconds="300"
           timeToLiveSeconds="600"/>
</ehcache>
 */

 /*
In Hibernate, caching is employed to reduce the number of database queries and improve application performance. Hibernate
    provides two main levels of caching:
First-Level Cache (Session-Scoped Cache)
    Scope and Lifetime: Associated with the Session object and exists only as long as the session is open.
    Purpose and Activation: Caches entities within the session to prevent repeated database calls for the same entity during
        the session's lifecycle. It is enabled by default and automatically managed by Hibernate.
    Usage: Benefits performance within a single unit of work. For example, if an entity is retrieved multiple times within
        the same session, it will be fetched from this cache after the first database query.
Second-Level Cache (SessionFactory-Scoped Cache)
    Scope and Lifetime: Associated with the SessionFactory and is shared across multiple sessions, lasting as long as the
        SessionFactory is open.
    Purpose and Activation: Caches entities (and optionally other data) at the application level to minimize database access
        across sessions. It is disabled by default and requires explicit configuration with a third-party cache provider.
    Usage: Most beneficial for frequently accessed data shared by multiple sessions. If an entity is cached here by one
        session, another session can retrieve it from the cache without hitting the database.
Key Differences Summarized:
    Feature         First-Level Cache               Second-Level Cache
    Scope           Session-scoped                  SessionFactory-scoped
    Lifetime        Tied to the session's lifecycle	Tied to the SessionFactory's lifecycle
    Sharing         Not shared across sessions      Shared across multiple sessions
    Configuration	Enabled by default              Disabled by default, requires config
    Purpose         Within a single session         Across multiple sessions
In summary, the first-level cache optimizes performance within a single session, while the second-level cache provides
    application-level caching for data shared across multiple sessions
 */
