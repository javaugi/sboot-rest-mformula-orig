/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

/**
 * A service for tracking and enforcing Responsible AI principles.
 */
interface ResponsibleAIService {

	void performPrivacyCheck(PatientInquiryRequest request);

	void logAiInteraction(PatientInquiryRequest request, PatientInquiryResponse response);

	void updateResponsibleAiIndex(PatientInquiryResponse response);

}
