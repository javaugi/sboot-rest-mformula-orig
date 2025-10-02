/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class StripeProperties {

	private String apiKey;

}
