package com.gaurav.order_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaurav.order_service.dto.InventoryResponse;
import com.gaurav.order_service.dto.OrderLineItemsDto;
import com.gaurav.order_service.dto.OrderRequest;
import com.gaurav.order_service.repository.OrderRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderRepository orderRepository;

	private static MockWebServer mockWebServer;

	@BeforeAll
	static void setup() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("inventory.service.url", () -> mockWebServer.url("/").toString());
	}

	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		InventoryResponse[] inventoryResponseArray = {
				new InventoryResponse("iphone_15", true)
		};

		mockWebServer.enqueue(new MockResponse()
				.setBody(objectMapper.writeValueAsString(inventoryResponseArray))
				.addHeader("Content-Type", "application/json"));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(orderRequestString))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, orderRepository.findAll().size());
	}

	private OrderRequest getOrderRequest() {
		OrderLineItemsDto orderLineItemsDto = new OrderLineItemsDto();
		orderLineItemsDto.setPrice(BigDecimal.valueOf(1200));
		orderLineItemsDto.setQuantity(1);
		orderLineItemsDto.setSkuCode("iphone_15");

		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setOrderLineItemsDtoList(List.of(orderLineItemsDto));
		return orderRequest;
	}

}
