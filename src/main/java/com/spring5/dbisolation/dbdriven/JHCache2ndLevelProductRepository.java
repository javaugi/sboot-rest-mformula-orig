/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import jakarta.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JHCache2ndLevelProductRepository extends JpaRepository<JPAHibernateCache2ndLevelProduct, Long> {

	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
	@Query("SELECT p FROM JPAHibernateCache2ndLevelProduct p WHERE p.price > :minPrice")
	List<JPAHibernateCache2ndLevelProduct> findExpensiveProducts(@Param("minPrice") BigDecimal minPrice);

}
