/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import static com.spring5.rediscache.proactivecachepopu.InventoryEvent.EventType.RESERVATION;
import com.spring5.repository.InventoryRepository;
import com.spring5.validatorex.InventoryException;
import static jakarta.persistence.GenerationType.UUID;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RedisInventoryService {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private final InventoryRepository repository;

    @Transactional
    public ReservationResult reserveVehicle(String vin, String dealerId, String customerId) {
        // Optimistic lock check
        long currentVersion = repository.getVersion(vin);

        if (!repository.isAvailable(vin, dealerId)) {
            return ReservationResult.failure("Vehicle not available");
        }

        var event
                = InventoryEvent.builder()
                        .eventId(UUID.toString())
                        .vin(vin)
                        .type(RESERVATION)
                        .dealerId(dealerId)
                        .details(Map.of("customerId", customerId))
                        .version(currentVersion + 1)
                        .build();

        try {
            kafkaTemplate.send("inventory-events", vin, event);
            return ReservationResult.success(event.getEventId());
        } catch (Exception e) {
            throw new InventoryException("Reservation failed", e);
        }
    }
}
