/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;

@Service
@Slf4j
public class RateLimiterRequestInterceptor implements HandlerInterceptor {
    // see RateLimitFilter on how to register this class to the filter

    private final RateLimiterRequestOnSize rateLimiter = new RateLimiterRequestOnSize();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        String userId = getUserId(request);
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User ID required");
            return false;
        }

        if (!rateLimiter.isAllowed(userId)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded");
            return false;
        }

        return true;
    }

    public boolean isAllowed(String userId) {
        return rateLimiter.isAllowed(userId);
    }

    // different ways to get userId - see methods below
    private String getUserId(HttpServletRequest request) {
        String userId = null;

        try {
            // get from header
            userId = request.getHeader("X-User-Id");
            if (userId != null) {
                return userId;
            }

            // or extract from token/session
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            userId = auth.getName();
            if (userId != null) {
                return userId;
            }

            // If using a custom UserDetails object:
            UserDetails user = (UserDetails) auth.getPrincipal();
            userId = user.getUsername(); // assuming you have getId()

        } catch (Exception e) {

        }

        return userId;
    }

    @RequestMapping("/api")
    public ResponseEntity<String> handleRequest(@RequestHeader("X-User-Id") String userId) {
        // Use the userId here
        return ResponseEntity.ok("User ID: " + userId);
    }

    // Spring Security 6+
    @GetMapping("/me")
    public ResponseEntity<String> getUserId(@AuthenticationPrincipal Jwt jwt) {
        String userId = (String) jwt.getHeader().get("X-User-Id"); // or jwt.getClaim("user_id");

        return ResponseEntity.ok("User ID: " + userId);
    }
}
