/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.veh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5.repository.ProductRepository;
import com.spring5.service.ProductServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductPatchUpdateTests {

	@Mock
	private ProductRepository productRepo;

	@Autowired
	private MockMvc mockMvc;

	// When using Mockito Use @InjectMocks to inject Mocked beans to following class
	@InjectMocks
	private ProductServiceImpl productService;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testNothing() {
	}

	@Test
	public void testPatchProduct() {
		/*
		 * mockMvc.perform(put("/restproduct/heavyresource/1")
		 * .contentType("application/json") .content(objectMapper.writeValueAsString(new
		 * Product(1, "Tom", "Jackson", 12, "heaven street")))
		 * ).andExpect(status().isOk()); //
		 */

	}

}
