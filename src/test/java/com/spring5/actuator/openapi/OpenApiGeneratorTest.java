/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.openapi;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@TestPropertySource(properties = {"springdoc.api-docs.path=/api-docs"})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = OpenApiTestConfig.class // Explicitly specify your main class
)
@TestPropertySource(properties = {
    "springdoc.api-docs.path=/api-docs",
    "springdoc.swagger-ui.enabled=true"
})
@Disabled
public class OpenApiGeneratorTest {

    @LocalServerPort
    private int port;

    //@Test
    public void generateOpenApiSpec() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        String openApiJson = restTemplate.getForObject(
                "http://localhost:8088/v3/api-docs",
                String.class
        );

        // Write to file
        Files.writeString(
                Paths.get("target/openapi.json"),
                openApiJson,
                StandardOpenOption.CREATE
        );
    }
}
