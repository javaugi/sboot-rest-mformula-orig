/*
 * Copyright (C) 2019 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.spring5.service;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
@Transactional
@Service
@RequiredArgsConstructor
public class ReactiveProductServiceImpl {

	private final ProductRepository productRepository;

	public Flux<Product> findAll() {
		return Flux.fromIterable(productRepository.findAll()).subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<Product> findProduct(Long id) {
		return Mono.fromCallable(() -> productRepository.findById(id).orElse(null))
			.subscribeOn(Schedulers.boundedElastic());
	}

}
