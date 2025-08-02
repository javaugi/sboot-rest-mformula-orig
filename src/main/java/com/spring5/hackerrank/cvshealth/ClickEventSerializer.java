/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

// --- Custom Serdes for ClickEvent and Map<String, Long> (using Jackson for simplicity) ---

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;

// In a real application, you'd likely use Avro/Protobuf with Schema Registry
public class ClickEventSerializer implements Serializer<ClickEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, ClickEvent data) {
        try { return objectMapper.writeValueAsBytes(data); }
        catch (IOException e) { throw new RuntimeException("Error serializing ClickEvent", e); }
    }
}
