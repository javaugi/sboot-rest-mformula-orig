/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.audit;

import com.spring5.EventBusConfig;
import com.spring5.MyApplication;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.bus.MBassador;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventBusHealthIndicator implements HealthIndicator {
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MyApplication.class, args);
        MBassador<Object> eventBus = context.getBean(EventBusConfig.MB_EVENT_BUS, MBassador.class);
        EventBusHealthIndicator main = new EventBusHealthIndicator(eventBus);
        log.info("Health {}", main.health());
    }

    private final MBassador<Object> eventBus;

    public EventBusHealthIndicator(@Qualifier(EventBusConfig.MB_EVENT_BUS) MBassador<Object> eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Health health() {
        try {
            // Simple ping test
            eventBus.publish(new PingEvent());
            return Health.up().build();
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .withDetail("message", "Event bus not functioning")
                .build();
        }
    }

    public static class PingEvent {}
    
    @EventListener
    public void onPing(PingEvent event) {
        log.info("PingEvent received at " + LocalDateTime.now());
    }
}
