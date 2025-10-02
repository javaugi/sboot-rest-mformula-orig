/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseReadinessHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		try {
			// Example check: simulate DB connection
			boolean dbIsUp = true; // Replace with real DB check
			if (dbIsUp) {
				return Health.up().withDetail("database", "Available").build();
			}
			else {
				return Health.down().withDetail("database", "Unavailable").build();
			}
		}
		catch (Exception e) {
			return Health.down(e).build();
		}
	}

}
