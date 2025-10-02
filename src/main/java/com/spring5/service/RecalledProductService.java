package com.spring5.service;

import com.spring5.entity.RecalledProduct;
import com.spring5.repository.RecalledProductRepository;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecalledProductService {

	private final RecalledProductRepository recalledProductRepository;

	public RecalledProduct save(RecalledProduct recalledProduct) {
		return recalledProductRepository.save(recalledProduct);
	}

	public Collection<RecalledProduct> getAllRecalledProducts() {
		return recalledProductRepository.findAll();
	}

	public Optional<RecalledProduct> findById(Integer id) {
		return recalledProductRepository.findById(id);
	}

}
