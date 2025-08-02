/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import static com.spring5.RedisConfig.RATE_LIMIT_CACHE_MAN;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;


@Configuration
public class RateLimitConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter());
        registrationBean.addUrlPatterns("/api/vehicles/*/updates");
        return registrationBean;
    }

    //*
    @Bean(name = RATE_LIMIT_CACHE_MAN)
    public CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        
        // Configure the cache for rate limiting
        CacheConfiguration<String, byte[]> config = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(
                String.class, 
                byte[].class,
                ResourcePoolsBuilder.heap(1000).build()
            ).build();
        
        // Create or get the cache
        cacheManager.createCache("rate-limit-buckets", 
            Eh107Configuration.fromEhcacheCacheConfiguration(config));
        
        return cacheManager;
    } 
    // */
    
    /**
     *
     * @return
     */
    @Bean
    public Bandwidth bandwidth() {
        return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
    }
    
    /*
// Example: 100 requests per minute per client
Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)))

// Example: 10 requests per second with burst of 20
Bandwidth.classic(20, Refill.greedy(10, Duration.ofSeconds(1)))    
    */

    // spring cloud api gateway
    @Bean  
    public ProxyManager<String> proxyManager(Bandwidth bandwidth, @Qualifier(RATE_LIMIT_CACHE_MAN) CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("rate-limit-buckets"));
    }
    
    
    /*
3. Alternative Implementations
If you don't want to use JCache (or want other backend options), Bucket4j supports several proxy managers:

a. Hazelcast:
java
@Bean
public ProxyManager<String> hazelcastProxyManager(HazelcastInstance hazelcastInstance) {
    return new HazelcastProxyManager<>(hazelcastInstance.getMap("rate-limit-buckets"));
}
b. Redis:
java
@Bean
public ProxyManager<String> redisProxyManager(RedisConnectionFactory factory) {
    return new RedisProxyManager<>(factory);
}
c. In-Memory (for simple cases):
java
@Bean 
public ProxyManager<String> inMemoryProxyManager() {
    return new InMemoryProxyManager<>();
}    
    */

    /*
    @Bean
    public KeyResolver vehicleKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(apiKey != null ? apiKey : ip);
        };
    }    
    // */
}
