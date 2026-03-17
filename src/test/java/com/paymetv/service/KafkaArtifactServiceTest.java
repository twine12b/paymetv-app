package com.paymetv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.Users;
import com.paymetv.app.service.JsonPayloadCreatorService;
import com.paymetv.app.service.KafkaArtifactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class KafkaArtifactServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaArtifactService kafkaArtifactService;

    @Autowired
    private JsonPayloadCreatorService jsonPayloadCreatorService;

    ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_product_json;
    private JsonNode expected_user_json;

    @Autowired
    private Artifact artifact;

    @Autowired
    private Users test_user;

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        // This test verifies that the Spring application context loads without errors
    }

    @BeforeEach
    void setup() throws IOException {
        test_user = new Users();
        test_user.setId(110L);
        test_user.setUsername("test user");
        test_user.setPassword("password");
        test_user.setEmail("test@test.com");

        artifact = new Artifact();
        artifact.setId(68L);
        artifact.setName("test product name");
        artifact.setDescription("test Description");
        artifact.setUser(test_user);
        artifact.setStatus(true);

        expected_product_json = mapper.readTree(getClass().getResource("/expected-artifact.json"));
        expected_user_json = mapper.readTree(getClass().getResource("/expected-users.json"));
    }

    @Test
    @DisplayName("Test Kafka Producer Service")
    public void testKafkaProducerService() {
        // Gather data to send
        String filename = "product_test.json";
        String topicName = "ml_streaming";
        Object object = artifact;
        JsonNode payload = jsonPayloadCreatorService.createJsonNode(object);

        // Publish message
        kafkaArtifactService.sendMessage(topicName, payload);

//        JsonNode product_json = mapper.valueToTree(product);
//        kafkaArtifactService.sendMessage("Hello World");
    }
}
