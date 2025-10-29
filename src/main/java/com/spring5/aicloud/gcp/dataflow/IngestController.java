
package com.spring5.aicloud.gcp.dataflow;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class IngestController {

    private final PubSubPublisher publisher;

    public IngestController(PubSubPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/ingest")
    public Map<String, String> ingest(@RequestBody Map<String, Object> payload) {
        String id = publisher.publish(payload.toString());
        return Map.of("status", "published", "id", id);
    }
}
