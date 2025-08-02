/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Objects;

// Matches your Cosmos DB container name
@Container(containerName = "Prescriptions")
public class PrescriptionOrder {
    
    @Id
    private String id; // This will be mapped to the Cosmos DB 'id' field
    @PartitionKey
    
    private String orderId; // Matches your Cosmos DB partition key path
    private String patientId;
    private String drugName;
    private int quantity;
    private Instant orderDate; // Use Instant for timestamps

    public PrescriptionOrder() {}

    public PrescriptionOrder(String id, String orderId, String patientId, String drugName, int quantity, Instant orderDate) {
        this.id = id;
        this.orderId = orderId;
        this.patientId = patientId;
        this.drugName = drugName;
        this.quantity = quantity;
        this.orderDate = orderDate;
    }

    // Getters and Setters (or use Lombok)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Instant getOrderDate() { return orderDate; }
    public void setOrderDate(Instant orderDate) { this.orderDate = orderDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionOrder that = (PrescriptionOrder) o;
        return quantity == that.quantity && Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(patientId, that.patientId) && Objects.equals(drugName, that.drugName) && Objects.equals(orderDate, that.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, patientId, drugName, quantity, orderDate);
    }

    @Override
    public String toString() {
        return "PrescriptionOrder{" +
               "id='" + id + '\'' +
               ", orderId='" + orderId + '\'' +
               ", patientId='" + patientId + '\'' +
               ", drugName='" + drugName + '\'' +
               ", quantity=" + quantity +
               ", orderDate=" + orderDate +
               '}';
    }
}
