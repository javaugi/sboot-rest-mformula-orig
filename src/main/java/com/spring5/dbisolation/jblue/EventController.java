/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventRecordRepository repo;

    public EventController(EventRecordRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public EventRecord get(@PathVariable String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @GetMapping("/health")
    public String quickHealth() {
        return "OK";
    }
}
