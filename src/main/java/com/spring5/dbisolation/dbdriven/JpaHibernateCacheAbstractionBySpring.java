/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

/*
Spring Cache Abstraction Example
You can also use Spring's cache abstraction with JPA:

Add dependency:

xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
Enable caching in your main class:

java
@SpringBootApplication
@EnableCaching
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
 */
@Service
@EnableCaching
public class JpaHibernateCacheAbstractionBySpring {

    @Autowired
    private JHCache2ndLevelProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    public JPAHibernateCache2ndLevelProduct getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = "products", key = "#id")
    public void updateProduct(Long id, JPAHibernateCache2ndLevelProduct product) {
        productRepository.save(product);
    }
}
