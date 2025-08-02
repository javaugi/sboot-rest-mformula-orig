/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import com.mongodb.bulk.UpdateRequest;
import com.mongodb.operation.UpdateOperation;
import org.springframework.stereotype.Service;

@Service
public class VehicleUpdateService {
 
    public UpdateOperation initiateUpdate(String vin, UpdateRequest request) {
        return null;
    }
    
    public UpdateOperation getOperationStatus(String operationId) {
        return null;
    }
}
