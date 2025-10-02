/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProcessResult {

	String failed;

	Claim claim;

}
