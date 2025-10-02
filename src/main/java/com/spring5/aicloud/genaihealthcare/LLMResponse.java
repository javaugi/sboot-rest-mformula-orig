/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LLMResponse {

	public String text;

	public double confidence; // heuristic; many providers don't directly give this

	public String modelName;

	public String modelVersion;

	public String provenance; // e.g., prompt id or trace id

	public String explanation; // human-readable explanation of decisions

	public LLMResponse(String text, double confidence, String modelName, String modelVersion, String provenance) {
		this.text = text;
		this.confidence = confidence;
		this.modelName = modelName;
		this.modelVersion = modelVersion;
		this.provenance = provenance;
	}

}
