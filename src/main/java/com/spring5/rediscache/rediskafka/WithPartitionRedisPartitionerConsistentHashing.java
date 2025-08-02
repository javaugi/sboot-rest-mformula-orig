/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.sparkproject.guava.hash.Hashing;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class WithPartitionRedisPartitionerConsistentHashing {
    private List<JedisPool> pools;
    
    public WithPartitionRedisPartitionerConsistentHashing(List<String> redisHosts) {
        pools = redisHosts.stream()
            .map(host -> new JedisPool(host, 6379))
            .collect(Collectors.toList());
    }
    
    public Jedis getConnection(String key) {
        int hash = Hashing.consistentHash(
            Hashing.sha256().hashString(key, StandardCharsets.UTF_8),
            pools.size()
        );
        return pools.get(hash).getResource();
    }    
}
