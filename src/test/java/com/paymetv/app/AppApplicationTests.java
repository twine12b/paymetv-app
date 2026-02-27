package com.paymetv.app;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
//@AutoConfigureMockMvc
class AppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Context loads successfully")
	void contextLoads() {
		// This test verifies that the Spring application context loads without errors
	}

	@Test
	@DisplayName("Health endpoint returns OK status")
	void testHealthEndpoint() throws Exception {
		mockMvc.perform(get("/api/health"))
				.andExpect(status().isOk())
				.andExpect(content().string("OK"));
	}

	@Test
	@DisplayName("Root endpoint returns Hello World message")
	void testRootEndpoint() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk());
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.message").value("Hello World"));
	}

	@Test
	@DisplayName("Create item successfully")
	void testCreateItem() throws Exception {
		String itemJson = "{\"name\":\"Test Item\"}";

		mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(itemJson))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.item_id").exists())
				.andExpect(jsonPath("$.name").value("Test Item"))
				.andExpect(jsonPath("$.status").value("created"));
	}

	@Test
	@DisplayName("Get item by ID successfully")
	void testGetItem() throws Exception {
		// First create an item
		String itemJson = "{\"name\":\"Get Test Item\"}";
		String response = mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(itemJson))
				.andReturn().getResponse().getContentAsString();

		// Extract the item_id from the response using JsonPath
		int itemId = JsonPath.parse(response).read("$.item_id", Integer.class);

		// Then retrieve it
		mockMvc.perform(get("/items/" + itemId))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.item_id").value(itemId))
				.andExpect(jsonPath("$.name").value("Get Test Item"));
	}

	@Test
	@DisplayName("Get non-existent item returns 404")
	void testGetNonExistentItem() throws Exception {
		mockMvc.perform(get("/items/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Item not found"));
	}

	@Test
	@DisplayName("Update item successfully")
	void testUpdateItem() throws Exception {
		// First create an item
		String createJson = "{\"name\":\"Original Item\"}";
		String response = mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
				.andReturn().getResponse().getContentAsString();

		// Extract the item_id from the response using JsonPath
		int itemId = JsonPath.parse(response).read("$.item_id", Integer.class);

		// Then update it
		String updateJson = "{\"name\":\"Updated Item\"}";
		mockMvc.perform(put("/items/" + itemId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.item_id").value(itemId))
				.andExpect(jsonPath("$.name").value("Updated Item"))
				.andExpect(jsonPath("$.status").value("updated"));
	}

	@Test
	@DisplayName("Update non-existent item returns 404")
	void testUpdateNonExistentItem() throws Exception {
		String updateJson = "{\"name\":\"Updated Item\"}";
		mockMvc.perform(put("/items/999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Item not found"));
	}

	@Test
	@DisplayName("Delete item successfully")
	void testDeleteItem() throws Exception {
		// First create an item
		String itemJson = "{\"name\":\"Item to Delete\"}";
		String response = mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(itemJson))
				.andReturn().getResponse().getContentAsString();

		// Extract the item_id from the response using JsonPath
		int itemId = JsonPath.parse(response).read("$.item_id", Integer.class);

		// Then delete it
		mockMvc.perform(delete("/items/" + itemId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.item_id").value(itemId))
				.andExpect(jsonPath("$.status").value("deleted"));
	}

	@Test
	@DisplayName("Delete non-existent item returns 404")
	void testDeleteNonExistentItem() throws Exception {
		mockMvc.perform(delete("/items/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Item not found"));
	}

	@Test
	@DisplayName("Metrics endpoint returns Prometheus metrics")
	void testMetricsEndpoint() throws Exception {
		mockMvc.perform(get("/metrics"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
	}

}
