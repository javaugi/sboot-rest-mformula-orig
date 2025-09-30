/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@MappedSuperclass
@SQLDelete(sql = "UPDATE #{#entityName} SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false") // This is the correct syntax
public abstract class BaseEntity {

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}

/*
// For temporal soft delete with history
CREATE TABLE customers_history AS TABLE customers WITH NO DATA;

CREATE OR REPLACE FUNCTION archive_deleted_customer() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.deleted = true AND OLD.deleted = false THEN
        INSERT INTO customers_history SELECT * FROM customers WHERE id = OLD.id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER customer_archive_trigger
AFTER UPDATE OF deleted ON customers
FOR EACH ROW EXECUTE FUNCTION archive_deleted_customer();

These examples cover a wide range of topics that would be relevant for a Backend Spring Boot Developer with Database
Experience interview, demonstrating both theoretical knowledge and practical implementation skills.
 */
