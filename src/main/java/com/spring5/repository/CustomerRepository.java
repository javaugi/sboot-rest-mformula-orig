/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// To query deleted records

	@Query("SELECT c FROM Customer c WHERE c.deleted = true")
	List<Customer> findAllDeleted();

	Optional<Customer> findByEmail(@Param("email") String email);

}

/*
 * // For temporal soft delete with history CREATE TABLE customers_history AS TABLE
 * customers WITH NO DATA;
 * 
 * CREATE OR REPLACE FUNCTION archive_deleted_customer() RETURNS TRIGGER AS $$ BEGIN IF
 * NEW.deleted = true AND OLD.deleted = false THEN INSERT INTO customers_history SELECT *
 * FROM customers WHERE id = OLD.id; END IF; RETURN NEW; END; $$ LANGUAGE plpgsql;
 * 
 * CREATE TRIGGER customer_archive_trigger AFTER UPDATE OF deleted ON customers FOR EACH
 * ROW EXECUTE FUNCTION archive_deleted_customer();
 * 
 * These examples cover a wide range of topics that would be relevant for a Backend Spring
 * Boot Developer with Database Experience interview, demonstrating both theoretical
 * knowledge and practical implementation skills.
 */
