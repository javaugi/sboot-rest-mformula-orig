package com.spring5.repository;

import com.spring5.entity.RecalledProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecalledProductRepository extends JpaRepository<RecalledProduct, Integer> {

}
