/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Service
public class WithPartitionRedisServerSideCluster {

	public void setup() {
		Set<HostAndPort> clusterNodes = new HashSet<>();
		clusterNodes.add(new HostAndPort("redis1.example.com", 6379));
		clusterNodes.add(new HostAndPort("redis2.example.com", 6379));
		clusterNodes.add(new HostAndPort("redis3.example.com", 6379));

		JedisCluster jedisCluster = new JedisCluster(clusterNodes);

		// Automatically routes to correct shard
		jedisCluster.set("order:123", "order_data");
		String value = jedisCluster.get("order:123");
	}

}
