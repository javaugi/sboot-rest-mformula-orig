/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
//B. Aspect-Oriented Programming (AOP) for Method Logging with Masking
@Component
public class PHIMaskingUtil {

    private static final List<String> PHI_FIELDS = Arrays.asList(
            "ssn", "socialSecurityNumber", "email", "phone",
            "dateOfBirth", "address", "medicalRecordNumber"
    );

    private final ObjectMapper objectMapper;

    public PHIMaskingUtil() {
        this.objectMapper = new ObjectMapper();

        // Configure Jackson to mask PHI fields
        PropertyFilter phiFilter = new SimpleBeanPropertyFilter() {
            @Override
            public void serializeAsField(Object pojo, JsonGenerator jgen,
                    SerializerProvider prov,
                    BeanPropertyWriter writer) throws Exception {
                if (PHI_FIELDS.contains(writer.getName())) {
                    jgen.writeStringField(writer.getName(), "***MASKED***");
                } else {
                    super.serializeAsField(pojo, jgen, prov, writer);
                }
            }
        };

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("phiFilter", phiFilter);
        objectMapper.setFilterProvider(filters);
    }

    public String maskObject(Object object) {
        try {
            // Add phi filter to the class
            objectMapper.addMixIn(object.getClass(), PHIMixin.class);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "Error masking object: " + e.getMessage();
        }
    }

    public String maskString(String text) {
        if (text == null) {
            return null;
        }

        // Simple regex-based masking for strings
        return text
                .replaceAll("\\b\\d{3}-\\d{2}-\\d{4}\\b", "***SSN_MASKED***")
                .replaceAll("\\b\\w+@\\w+\\.\\w+\\b", "***EMAIL_MASKED***")
                .replaceAll("\\b\\d{3}-\\d{3}-\\d{4}\\b", "***PHONE_MASKED***");
    }

    // Mixin class for Jackson filtering
    @JsonFilter("phiFilter")
    private abstract static class PHIMixin {
    }
}
