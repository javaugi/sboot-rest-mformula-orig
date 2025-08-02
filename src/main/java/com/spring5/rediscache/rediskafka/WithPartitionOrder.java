/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class WithPartitionOrder {
    String id;
    String orderid;
    String customerId;
    
    public static WithPartitionOrder fromJson(String json) {
        return WithPartitionOrder.builder().build();
    }
    public static String toJson() {
        return WithPartitionOrder.builder().toString();
    }
}
