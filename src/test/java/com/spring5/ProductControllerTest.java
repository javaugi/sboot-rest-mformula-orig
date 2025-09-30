/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import com.spring5.dao.ProductDto;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    //@Test
    public void testGetById_PathVariable() {
        webTestClient.get()
                .uri("/restproducts/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Product ID: 1");
    }

    //@Test
    public void testGetByType_RequestParam() {
        webTestClient.get()
                .uri(uriBuilder
                        -> uriBuilder.path("/restproducts")
                        .queryParam("type", "book")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Product type: book");
    }

    //@Test
    public void testCreate_RequestBody() {
        ProductDto dto = new ProductDto();
        dto.setName("Gadget");
        dto.setPrice(new BigDecimal(49.99));

        webTestClient.post()
                .uri("/restproducts")
                @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)            .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Created: Gadget");
    }

    //@Test
    public void testHeader_RequestHeader() {
        webTestClient.get()
                .uri("/restproducts/headers")
                .header("X-Request-ID", "abc-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Header: abc-123");
    }

    //@Test
    public void testFileUpload_RequestPart() {
        webTestClient.post()
                .uri("/restproducts/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file",
                        new ByteArrayResource("test content".getBytes()) {
                    @Override
                    public String getFilename() {
                        return "test.txt";
                    }
                }))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Uploaded: test.txt");
    }
}

/*
Parameter Type	Method Used in Test
@PathVariable	.uri("/restproducts/{id}", 1)
@RequestParam	.queryParam("type", "book")
@RequestBody	.bodyValue(dto) with JSON
@RequestHeader	.header("X-Request-ID", "...")
@RequestPart	BodyInserters.fromMultipartData(...)
 */
