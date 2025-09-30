/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import com.spring5.rediscache.MedicalFileMetadata;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
// https://github.com/javaugi/redis-caching-java-spring-boot
// https://github.com/javaugi/spring-boot-redis-example
public class RedisConfig extends RedisBaseConfig {

    public static final String REDIS_TPL = "strObjRedisTemplate";
    public static final String REDIS_TPL_STR = "strStrRedisTemplate";
    public static final String REDIS_TPL_MFILE = "strMedFileRedisTemplate";
    public static final String RATE_LIMIT_CACHE_MAN = "rateLimitCacheMan";

    @Primary
    @Bean(name = REDIS_TPL)
    public RedisTemplate<String, Object> redisObjectTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean(name = REDIS_TPL_STR)
    public RedisTemplate<String, String> redisStrTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(name = REDIS_TPL_MFILE)
    public RedisTemplate<String, MedicalFileMetadata> redisMedFileTemplate() {
        RedisTemplate<String, MedicalFileMetadata> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
