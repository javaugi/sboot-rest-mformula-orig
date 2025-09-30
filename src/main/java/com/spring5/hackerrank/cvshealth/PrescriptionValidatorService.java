/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.neo4j.driver.exceptions.TransientException;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionValidatorService {

    private final KafkaProducer<String, String> kafkaProducer;

    public PrescriptionValidatorService(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public boolean validateAndPublish(Prescription prescription) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                if (validate(prescription)) {
                    kafkaProducer.send(
                            new ProducerRecord<>("prescriptions" + prescription.getId(), "VALIDATED"));
                    return true;
                }
                return false;
            } catch (TransientException ex) {
                attempts++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
        return false;
    }

    private boolean validate(Prescription prescription) {
        // Call external service
        return true; // Simplified
    }
}
