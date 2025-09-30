/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author javau
 */
public class HashMapDeserializer implements Deserializer<Map<String, Long>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Long> deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing HashMap", e);
        }
    }
}
