/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JHCache2ndLevelProductService {

    @Autowired
    private JHCache2ndLevelProductRepository productRepository;

    @Transactional
    public void demonstrateSecondLevelCache() {
        // First call - hits database
        JPAHibernateCache2ndLevelProduct product1 = productRepository.findById(1L).orElse(null);
        System.out.println("First call: " + product1.getName());
    }

    @Transactional
    public void demonstrateSecondLevelCacheAcrossSessions() {
        // Second call in different transaction - hits second-level cache
        JPAHibernateCache2ndLevelProduct product2 = productRepository.findById(1L).orElse(null);
        System.out.println("Second call: " + product2.getName());
    }

    public void sequence3ServiceExample() {
        // this service
    }

    public void sequence2AnnotateEntity() {
        // see JPAHibernateCache2ndLevelProduct  @org.hibernate.annotations.Cache(usage =
        // CacheConcurrencyStrategy.READ_WRITE)
    }

    public void sequence1Cache2ndLevelEnable() {
        /*
    <!-- For Ehcache as second-level cache provider -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-ehcache</artifactId>
    </dependency>
    <dependency>
        <groupId>net.sf.ehcache</groupId>
        <artifactId>ehcache</artifactId>
    </dependency>
    Configure in application.properties:

    properties
    # Enable second-level cache
    spring.jpa.properties.hibernate.cache.use_second_level_cache=true
    spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory

    # Enable query cache
    spring.jpa.properties.hibernate.cache.use_query_cache=true

    # Show SQL for demonstration
    spring.jpa.show-sql=true
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

    }
}
