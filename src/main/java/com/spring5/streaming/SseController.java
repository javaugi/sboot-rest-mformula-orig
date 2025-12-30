/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.streaming;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SseController {

    @GetMapping(path = "/stream-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSseEvents() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .data("SSE Event #" + i + " at " + System.currentTimeMillis())
                            .id(String.valueOf(i))
                            .name("message");

                    emitter.send(event);
                    Thread.sleep(1000); // Send every second
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onCompletion(() -> executor.shutdown());
        return emitter;
    }
}
