/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.audit;

import java.util.List;

public class OrderReceivedEvent {

    private String orderId;
    private List<AuditOrder> items;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<AuditOrder> getItems() {
        return items;
    }

    public void setItems(List<AuditOrder> items) {
        this.items = items;
    }
}
