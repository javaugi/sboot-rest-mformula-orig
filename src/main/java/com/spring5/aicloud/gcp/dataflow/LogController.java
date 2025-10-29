/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.gcp.dataflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogController {

    @Autowired
    private PubSubTemplate pubSubTemplate;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String topicName = "app-logs";

    @PostMapping("/logs")
    public String publishLog(@RequestBody Map<String, Object> payload) throws Exception {
        String json = mapper.writeValueAsString(payload);
        pubSubTemplate.publish(topicName, json);
        return "Published to Pub/Sub: " + json;
    }
}
