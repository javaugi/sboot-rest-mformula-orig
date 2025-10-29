/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.jcacheimpl;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import java.util.List;
//import org.hibernate.annotations.Cache;
//import javax.cache.CacheManager;
//import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheRefresher {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CacheManager cacheManager;

    //@Scheduled(fixedRate = 3600000) // Refresh every hour
    public void preloadProductCache() {
        List<Product> products = productRepository.findAll();
        Cache productsCache = cacheManager.getCache("products");
        if (productsCache != null) {
            products.forEach(p -> productsCache.put(p.getId(), p));
        }
    }

    /*
    public void refreshCache(String cache, Entity entity) {
        List<Product> products = productRepository.findAll();
        Cache productsCache = cacheManager.getCache("products");
        if (productsCache != null) {
            products.forEach(p -> productsCache.put(p.getId(), p));
        }
    }
    // */
}
