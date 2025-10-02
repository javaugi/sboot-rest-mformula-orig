/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

/*
Bonus: Advanced PostgreSQL Features
10. How would you implement soft delete with PostgreSQL and JPA?
Detailed Description:
    Soft delete marks records as deleted without physically removing them, allowing for data recovery and audit trails.
        This can be implemented with a deleted flag or timestamp.
 */
public class SoftDeleteWithPostgreSQLJPA {

	// see BaseEntity, Cusomter and CustomerRepository

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
