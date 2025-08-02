/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mongordb")
public class MongoRDBProductController {
    @Autowired
    private MongoRDBRepository productRepo;

    @GetMapping("/products")
    public Flux<MongoRDBProduct> getAllProducts() {
        return productRepo.findAll();
    }

    @PostMapping("/product")
    public Mono<MongoRDBProduct> addProduct(@RequestBody MongoRDBProduct product) {
        return productRepo.save(product);
    }    
    
    @PostMapping
    public Mono<MongoRDBProduct> create(@RequestBody MongoRDBProduct product) {
        return productRepo.save(product);
    }
}
