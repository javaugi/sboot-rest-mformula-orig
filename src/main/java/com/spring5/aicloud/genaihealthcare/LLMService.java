/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

/**
 * A mock service interface for interacting with a Generative AI model. In a real
 * application, this would integrate with a model API like Google's Gemini.
 */
interface LLMService {

	PatientInquiryResponse getPersonalizedResponse(PatientInquiryRequest request);

}
