/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;

public class ClickEventDeserializer implements Deserializer<ClickEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ClickEvent deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, ClickEvent.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing ClickEvent", e);
        }
    }
}
