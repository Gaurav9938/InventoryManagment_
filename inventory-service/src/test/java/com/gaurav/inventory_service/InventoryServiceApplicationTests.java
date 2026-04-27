package com.gaurav.inventory_service;

import com.gaurav.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldCheckInventory() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
						.param("skuCode", "iphone_15", "iphone_15_red"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].skuCode").value("iphone_15"))
				.andExpect(jsonPath("$[0].inStock").value(true))
				.andExpect(jsonPath("$[1].skuCode").value("iphone_15_red"))
				.andExpect(jsonPath("$[1].inStock").value(false));
	}

}
