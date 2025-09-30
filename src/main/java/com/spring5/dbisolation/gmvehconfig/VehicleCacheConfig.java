/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import com.spring5.RedisBaseConfig;
import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
@EnableCaching
public class VehicleCacheConfig extends RedisBaseConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder
                -> builder
                        .withCacheConfiguration(
                                "inventory",
                                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                        .withCacheConfiguration(
                                "configurations",
                                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1L)));
    }
}
