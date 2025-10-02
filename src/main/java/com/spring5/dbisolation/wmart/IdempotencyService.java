/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

// simplified idempotency service using Redis (Lettuce or Spring Data Redis)
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

	private final StringRedisTemplate redis;

	public IdempotencyService(StringRedisTemplate redis) {
		this.redis = redis;
	}

	public boolean claim(String key, String payloadHash, Duration ttl) {
		// set if absent returns true if not present
		Boolean ok = redis.opsForValue().setIfAbsent(key, payloadHash, ttl);
		return Boolean.TRUE.equals(ok);
	}

	public void saveResponse(String key, String responseJson) {
		redis.opsForValue().set(key + ":resp", responseJson);
	}

	public String getResponse(String key) {
		return redis.opsForValue().get(key + ":resp");
	}

}
