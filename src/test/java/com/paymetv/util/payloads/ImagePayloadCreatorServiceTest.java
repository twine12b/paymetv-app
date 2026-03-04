package com.paymetv.util.payloads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.domain.Product;
import com.paymetv.app.domain.Users;
import com.paymetv.app.service.ImagePayloadCreatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(classes=ImagePayloadCreatorService.class)
@AutoConfigureMockMvc
class ImagePayloadCreatorServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImagePayloadCreatorService imagePayloadCreatorService;

    private Product product;
    private Users test_user;

    ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_product_json;
    private JsonNode expected_user_json;

    @Test
    void createImagePayload() throws IllegalStateException {
    }

    @BeforeEach
    void setup() throws IOException {
        test_user = new Users();
        test_user.setId(110);
        test_user.setUsername("test user");
        test_user.setPassword("password");
        test_user.setEmail("test@test.com");

        product = new Product();
        product.setId(68);
        product.setName("test product name");
        product.setDescription("test Description");
        product.setUser(test_user);
        product.setStatus(true);

        expected_product_json = mapper.readTree(getClass().getResource("/expected-product.json"));
        expected_user_json = mapper.readTree(getClass().getResource("/expected-users.json"));
    }

    @Test
    @DisplayName("create a Json file from a Product object and a filename [string value]")
    void createProductJsonFile() throws JsonProcessingException {

        String filename = "product_test.json";
        Object object = product;

        JsonNode actual = imagePayloadCreatorService.createJsonNode(object);
        assertNotNull(actual);
        assertEquals(expected_product_json, actual);
    }

    @Test
    @DisplayName("create a Json file from a Users object and a filename [string value]")
    void createUsersJsonFile() throws JsonProcessingException {
        String filename = "user_test.json";
        Object object = test_user;

        JsonNode actual = imagePayloadCreatorService.createJsonNode(object);
        assertNotNull(actual);
        assertEquals(expected_user_json, actual);
    }
}
