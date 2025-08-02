/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class WithPartitionRedisClientSide {
    // Create connection pools for each Redis instance

    JedisPool pool1 = new JedisPool("redis1.example.com", 6379);
    JedisPool pool2 = new JedisPool("redis2.example.com", 6379);

    public Jedis getShard(String key) {
        // Simple hash-based partitioning
        int hash = Math.abs(key.hashCode());
        return (hash % 2 == 0) ? pool1.getResource() : pool2.getResource();
    }

    public void setData(String key, String value) {
        try (Jedis jedis = getShard(key)) {
            jedis.set(key, value);
        }
    }
}
