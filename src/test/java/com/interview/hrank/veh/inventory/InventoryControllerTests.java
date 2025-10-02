package com.interview.hrank.veh.inventory;

import com.spring5.controller.InventoryController;
import com.spring5.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@Disabled("Temporarily disabled for CICD")
public class InventoryControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	InventoryController inventoryController;

	@Mock
	ProductService productService;

	@BeforeEach
	public void before() {
		mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
	}

	@SneakyThrows
	@Test
	public void shouldCallController() {
		mockMvc.perform(get("/api/inventory/product").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

}
