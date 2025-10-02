/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("app")
public class AppHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		// Example: perform lightweight checks — DB connectivity, event hub status, CPU
		// threshold
		boolean dbOk = true; // ping DB or do a small read
		if (!dbOk) {
			return Health.down().withDetail("db", "unreachable").build();
		}
		return Health.up().withDetail("version", "1.0.0").build();
	}

}
