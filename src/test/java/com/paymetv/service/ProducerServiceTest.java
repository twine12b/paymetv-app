package com.paymetv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymetv.app.domain.Product;
import com.paymetv.app.domain.Users;
import com.paymetv.app.service.JsonPayloadCreatorService;
import com.paymetv.app.service.ProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

//@SpringBootTest(SpringBootTestclasses = {ProducerService.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProducerServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProducerService producerService;

    @Autowired
    JsonPayloadCreatorService jsonPayloadCreatorService;

    ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_product_json;
    private JsonNode expected_user_json;

    private Product test_product;
    private Users test_user;

    @BeforeEach
    public void setup() throws IOException {
        test_user = new Users(110, "test user", "password", "test@test.com");
        test_product = new Product(68, "test product name", "test Description", test_user, true);

//        expected_user_json = mapper.readTree(getClass().getResource("/expected_user_json"));
//        expected_product_json = mapper.readTree(getClass().getResource("/expected_product_json"));
    }

    @Test
//    @DisplayName("Context loads successfully")
    void jsonProducer() {
        JsonNode payload = jsonPayloadCreatorService.createJsonNode(test_product);
        String topic = "file-uploaded.json";

        producerService.sendMessage(topic, payload);

    }
}
