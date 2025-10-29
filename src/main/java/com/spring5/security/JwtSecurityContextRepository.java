/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security;

import com.spring5.entity.User;
import com.spring5.repository.UserRepository;
import com.spring5.utils.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
/*
7) Security — SecurityConfig.java and a reactive SecurityContextRepository that checks JWT
JwtSecurityContextRepository.java — extracts Authorization header, validates token, sets Authentication:
 */
@Component
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;

    public JwtSecurityContextRepository(JwtUtils jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // Not used (stateless)
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token).block()) {
            return Mono.empty();
        }
        String username = jwtUtil.getUsername(token);

        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isPresent()) {
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication auth = new UsernamePasswordAuthenticationToken(username, token, authorities);
            //return new SecurityContextImpl(auth);
            return Mono.just(new SecurityContextImpl(auth));
        } else {
            return Mono.empty();
        }

        // Optionally fetch user details (e.g. roles) from the DB
        /*
        return userRepository.findByUsername(username)
                .map(user -> {
                    // For simplicity, grant a single ROLE_USER
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                    Authentication auth = new UsernamePasswordAuthenticationToken(username, token, authorities);
                    return new SecurityContextImpl(auth);
                });
        // */
    }
}
