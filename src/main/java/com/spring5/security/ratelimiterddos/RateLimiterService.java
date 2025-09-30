/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    // see RateLimitConfig for details
    private final ProxyManager<String> proxyManager;
    private final Bandwidth bandwidth;

    public boolean tryConsume(String clientId, int tokens) {
        BucketConfiguration config = BucketConfiguration.builder().addLimit(bandwidth).build();
        Bucket bucket = proxyManager.builder().build(clientId, config);
        return bucket.tryConsume(tokens);
    }
}
