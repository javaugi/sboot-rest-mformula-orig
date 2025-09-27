/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

//2. Realistic DxCG Integration Implementation
//Here's how you would actually integrate with DxCG services:
//A. SOAP Client Implementation
@Configuration
public class DxcgSoapConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.verisk.dxcg.ws");
        return marshaller;
    }

    /*
    @Bean
    public DxcgSoapClient dxcgSoapClient(Jaxb2Marshaller marshaller) {
        DxcgSoapClient client = new DxcgSoapClient();
        client.setDefaultUri("https://ws.verisk.com/DxcgService");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
    // */
}
