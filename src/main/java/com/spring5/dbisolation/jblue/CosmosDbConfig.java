/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.spring.data.cosmos.CosmosFactory;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import org.springframework.beans.factory.annotation.Value;

// @Configuration
// @EnableCosmosRepositories(basePackages = "com.example.repository")
public class CosmosDbConfig extends AbstractCosmosConfiguration {

	@Value("${cosmos.uri}")
	private String uri;

	@Value("${cosmos.key}")
	private String key;

	@Value("${cosmos.database}")
	private String database;

	@Override
	public CosmosAsyncClient cosmosAsyncClient(CosmosClientBuilder cosmosClientBuilder) {
		return cosmosClientBuilder.consistencyLevel(ConsistencyLevel.SESSION)
			.contentResponseOnWriteEnabled(true)
			.buildAsyncClient();
	}

	@Override
	// @Bean
	public CosmosFactory cosmosFactory(CosmosAsyncClient asynClient) {
		return new CosmosFactory(asynClient, database);
	}

	@Override
	// @Bean
	public CosmosConfig cosmosConfig() {
		return CosmosConfig.builder().enableQueryMetrics(true).build();
	}

	@Override
	public String getDatabaseName() {
		return database;
	}

}
