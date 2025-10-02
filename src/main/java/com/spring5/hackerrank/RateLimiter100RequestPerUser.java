/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimiter100RequestPerUser {

	private static final int MAX_REQUESTS_PER_MINUTE = 100;

	private static final long ONE_MINUTE_TIME_WINDOW_MILLIS = 60 * 1000L;

	// Key = userId, Value = timestamps of requests
	private final ConcurrentHashMap<String, Deque<Long>> userIdTimeWindowMap = new ConcurrentHashMap<>();

	public boolean isAllowed(String userId) {
		long now = Instant.now().toEpochMilli();
		Deque<Long> timestamps = userIdTimeWindowMap.computeIfAbsent(userId, k -> new ArrayDeque<>());

		synchronized (timestamps) {
			// Remove timestamps older than 1 minute
			while (!timestamps.isEmpty() && now - timestamps.peekFirst() > ONE_MINUTE_TIME_WINDOW_MILLIS) {
				timestamps.pollFirst();
			}

			if (timestamps.size() < MAX_REQUESTS_PER_MINUTE) {
				timestamps.addLast(now);
				userIdTimeWindowMap.put(userId, timestamps);
				return true; // Allow request
			}
			else {
				return false; // Too many requests
			}
		}
	}

	public static void main(String[] args) {
		RateLimiter100RequestPerUser m = new RateLimiter100RequestPerUser();
		List<String> twoUserList = List.of("user1", "user2");
		// First 100 requests should pass for each user
		for (String userId : twoUserList) {
			log.info(" userId=" + userId + "-successful=" + m.runAllowRequest(userId, (MAX_REQUESTS_PER_MINUTE + 5)));
		}
	}

	private boolean runAllowRequest(String userId, int tokenCount) {
		boolean returnValue = true;

		for (int i = 0; i < tokenCount; i++) {
			returnValue = returnValue && isAllowed(userId);
		}

		return returnValue;
	}

}
