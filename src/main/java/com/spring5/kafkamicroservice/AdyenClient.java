/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.adyen.Client;
import com.adyen.enums.Environment;
import org.springframework.stereotype.Service;

@Service
public class AdyenClient extends Client{
    private String apiKey;
    private String merchantAccount;
    private String environment;
    private String endpoint;
    
    public AdyenClient(String apiKey, Environment env) {
        super(apiKey, env);
        this.apiKey = apiKey;
        this.environment = env.name();
    }
    
    public AdyenPaymentResponse getPaymentDetails(String pspReference) {
        return new AdyenPaymentResponse(pspReference);
    }
}
