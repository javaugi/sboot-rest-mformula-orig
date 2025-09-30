/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.idempotency;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/idempotencyapi/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<IdemPotentOrder> createOrder(@RequestBody OrderRequest orderRequest) {
        // Process order creation
        IdemPotentOrder order = processOrderCreation(orderRequest);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdemPotentOrder> updateOrder(
            @PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        // Process order update
        IdemPotentOrder order = processOrderUpdate(id, orderRequest);
        return ResponseEntity.ok(order);
    }

    private IdemPotentOrder processOrderCreation(OrderRequest request) {
        // Your business logic here
        return new IdemPotentOrder(1L, request.getProductId(), request.getQuantity(), "CREATED");
    }

    private IdemPotentOrder processOrderUpdate(Long id, OrderRequest request) {
        // Your business logic here
        return new IdemPotentOrder(id, request.getProductId(), request.getQuantity(), "UPDATED");
    }
}

/*
Spring Boot REST API with Idempotency-Key Support
Here's a complete example of implementing idempotency in a Spring Boot REST API using Idempotency-Key headers for POST and PUT requests.

1. Create Idempotency Key Filter
First, create a filter to check for and process idempotency keys:

java
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final Map<String, Object> idempotencyStore = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
        throws ServletException, IOException {

        // Only check for POST and PUT requests
        if ("POST".equalsIgnoreCase(request.getMethod()) ||
            "PUT".equalsIgnoreCase(request.getMethod())) {

            String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

            if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
                // Check if we've seen this key before
                if (idempotencyStore.containsKey(idempotencyKey)) {
                    // Return the cached response
                    Object cachedResponse = idempotencyStore.get(idempotencyKey);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(cachedResponse.toString());
                    return;
                }

                // Wrap the response to cache it
                ContentCachingResponseWrapper responseWrapper =
                    new ContentCachingResponseWrapper(response);

                try {
                    filterChain.doFilter(request, responseWrapper);

                    // Only cache successful responses (2xx)
                    if (responseWrapper.getStatus() >= 200 &&
                        responseWrapper.getStatus() < 300) {
                        byte[] responseBody = responseWrapper.getContentAsByteArray();
                        idempotencyStore.put(idempotencyKey, new String(responseBody));
                    }

                    // Write the response to the actual client
                    responseWrapper.copyBodyToResponse();
                } finally {
                    responseWrapper.copyBodyToResponse();
                }
                return;
            }
        }

        // No idempotency key or not POST/PUT - proceed normally
        filterChain.doFilter(request, response);
    }
}
2. Create Response Wrapper
Create a response wrapper to cache the response content:

java
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ContentCachingResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private PrintWriter writer;

    public ContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(content);
        }
        return writer;
    }

    @Override
    public String toString() {
        flushBuffer();
        return content.toString();
    }

    public byte[] getContentAsByteArray() {
        flushBuffer();
        return content.toByteArray();
    }

    private void flushBuffer() {
        if (writer != null) {
            writer.flush();
        }
    }
}
3. Create REST Controller
Here's a sample controller that works with the idempotency filter:

java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        // Process order creation
        Order order = processOrderCreation(orderRequest);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderRequest orderRequest) {
        // Process order update
        Order order = processOrderUpdate(id, orderRequest);
        return ResponseEntity.ok(order);
    }

    private Order processOrderCreation(OrderRequest request) {
        // Your business logic here
        return new Order(1L, request.getProductId(), request.getQuantity(), "CREATED");
    }

    private Order processOrderUpdate(Long id, OrderRequest request) {
        // Your business logic here
        return new Order(id, request.getProductId(), request.getQuantity(), "UPDATED");
    }
}
4. DTO Classes
Here are the DTO classes used in the example:

java
public class OrderRequest {
    private Long productId;
    private int quantity;

    // Getters and setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

public class Order {
    private Long id;
    private Long productId;
    private int quantity;
    private String status;

    public Order(Long id, Long productId, int quantity, String status) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
}
5. Testing the API
You can test the API with curl commands:

First request (will be processed):
bash
curl -X POST \
  http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: abc123' \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
Second request with same key (will return cached response):
bash
curl -X POST \
  http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: abc123' \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
Important Considerations
    1. Storage: This example uses an in-memory map which is not suitable for production. In a real application, you should use a distributed cache or database.
    2. Expiration: You should implement expiration for idempotency keys to prevent the store from growing indefinitely.
    3. Request Matching: For a more robust solution, you might want to consider matching both the idempotency key AND the request content.
    4. Error Handling: Consider how to handle cases where the same idempotency key is used with different request bodies.
    5. HTTP Methods: While this example covers POST and PUT, you might want to include PATCH as well depending on your requirements.
This implementation ensures that multiple identical requests with the same idempotency key will produce the same result without side effects.
 */
