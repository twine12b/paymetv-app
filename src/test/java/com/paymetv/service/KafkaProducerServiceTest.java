package com.paymetv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.domain.Product;
import com.paymetv.app.domain.Users;
import com.paymetv.app.service.JsonPayloadCreatorService;
import com.paymetv.app.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

@SpringBootTest(classes = {KafkaProducerService.class, JsonPayloadCreatorService.class})
@DirtiesContext
@AutoConfigureMockMvc
public class KafkaProducerServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private JsonPayloadCreatorService jsonPayloadCreatorService;

    ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_product_json;
    private JsonNode expected_user_json;

    private Product product;
    private Users test_user;

//    @Test
//    @DisplayName("Context loads successfully")
//    void contextLoads() {
//        // This test verifies that the Spring application context loads without errors
//    }

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
    @DisplayName("Test Kafka Producer Service")
    public void testKafkaProducerService() {
        // Gather data to send
        String filename = "product_test.json";
        String topicName = "ml_streaming";
        Object object = product;
        JsonNode payload = jsonPayloadCreatorService.createJsonNode(object);

        // Publish message
        kafkaProducerService.sendMessage(topicName, payload);

//        JsonNode product_json = mapper.valueToTree(product);
//        kafkaProducerService.sendMessage("Hello World");
    }
}
