/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service    
@Transactional(isolation = Isolation.SERIALIZABLE)
public class PrescriptionOrderService {
    public void processOrder(PrescriptionData order) {
    // Deduct inventory
    // Process payment
    // Log audit trail
    }    
}
