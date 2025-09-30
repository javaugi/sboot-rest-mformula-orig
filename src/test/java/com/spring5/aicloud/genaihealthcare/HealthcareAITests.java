/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HealthcareAITests {

    @Autowired
    private ResponsibleAIIndexService aiService;

    // @Test
    public void testResponsibleAIIndexCreation() {
        ResponsibleAIIndex index
                = ResponsibleAIIndex.builder()
                        .modelName("patient_diagnosis_model")
                        .transparencyScore(8.5)
                        .fairnessScore(9.0)
                        .privacyScore(9.5)
                        .regulatoryAlignmentScore(8.0)
                        .build();

        assertNotNull(index);
        assertEquals("COMPLIANT", index.getComplianceStatus());
        assertTrue(index.getOverallScore() >= 8.0);
    }

    // @Test
    public void testAIService() {
        ResponsibleAIIndex index
                = ResponsibleAIIndex.builder()
                        .modelName("test_model")
                        .transparencyScore(7.0)
                        .fairnessScore(8.0)
                        .privacyScore(9.0)
                        .regulatoryAlignmentScore(8.0)
                        .build();

        ResponsibleAIIndex saved = aiService.saveOrUpdateIndex(index);
        assertNotNull(saved.getId());
        assertEquals("test_model", saved.getModelName());
    }
}
