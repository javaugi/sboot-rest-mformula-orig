/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.apigateway;

// @SpringBootApplication
// @EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		// SpringApplication.run(ApiGatewayApplication.class, args);
	}

}

/*
 * In the context of API gateways and service discovery in microservices architectures,
 * the distinction between client-side and server-side lookup (discovery) primarily lies
 * in where the logic for finding and connecting to service instances resides. Here's a
 * breakdown: 1. Client-Side Service Discovery: Responsibility: The client is responsible
 * for querying a service registry to obtain a list of available service instances and
 * then selecting one to send requests to. How it works: The client, equipped with a
 * discovery client library, directly interacts with the service registry (e.g., Netflix
 * Eureka). The client receives the list of available service instances directly from the
 * registry. The client then applies a load-balancing algorithm (e.g., round-robin) to
 * choose a specific instance to send the request to. Examples: Netflix Eureka
 * (historically used with Spring Cloud Ribbon), Spring Cloud LoadBalancer. 2. Server-Side
 * Service Discovery: Responsibility: An intermediary (like a load balancer or API
 * gateway) handles the service discovery and routes requests on behalf of the client. How
 * it works: The client sends the request to the intermediary, and the intermediary
 * queries the service registry to find an appropriate service instance. Examples: API
 * Gateway, Load Balancer in conjunction with a service registry like Consul.
 * 
 * API Gateway's Role: An API gateway can employ either client-side or server-side service
 * discovery. When integrated with a service registry like Spring Cloud Discovery (which
 * often uses frameworks like Eureka or Consul), the gateway can: Route requests
 * dynamically: By discovering and configuring routes based on the microservices
 * registered in the registry. Perform load balancing: Distribute traffic across multiple
 * instances of microservices for optimal performance and availability. Centralize
 * management: Monitor and manage microservices from a single point of control.
 * 
 * Key Differences Summarized: Feature Client-Side Discovery Server-Side Discovery
 * Responsibility Client Server (Load Balancer/API Gateway) Logic Location Client
 * Server/Intermediary Direct Interaction Client queries registry directly Client sends
 * requests to intermediary Load Balancing Client handles Intermediary handles
 * 
 * In essence, with client-side discovery, the "lookup" happens within the client
 * application, while with server-side discovery, the "lookup" is handled by an
 * intermediary on the server side. An API gateway can be a powerful tool for implementing
 * service discovery, offering dynamic routing, load balancing, and centralized
 * management, often leveraging server-side discovery patterns
 */
