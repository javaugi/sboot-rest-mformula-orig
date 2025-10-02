/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HccContribution {

	private String hccCode;

	private String description;

	private Double weight;

	private Double contribution;

}
