package com.paymetv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.domain.Users;
import com.paymetv.app.service.JsonPayloadCreatorService;
import com.paymetv.app.service.KafkaArtifactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for KafkaArtifactService.
 *
 * Uses @SpringBootTest to load the full application context.
 * Mocks KafkaTemplate to avoid requiring a real Kafka broker.
 */
@SpringBootTest(classes = AppApplication.class)
public class KafkaArtifactServiceTest {

    @Autowired
    private KafkaArtifactService kafkaArtifactService;

    @Autowired
    private JsonPayloadCreatorService jsonPayloadCreatorService;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_product_json;
    private JsonNode expected_user_json;

    // Entity instances - NOT autowired (entities are not Spring beans)
    private Artifact artifact;
    private Users test_user;
    private ImageFace test_image_face;

    /**
     * Configure test properties to disable Kafka auto-configuration issues.
     */
    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        // Use embedded Kafka or disable auto-configuration if needed
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        // Verify that Spring context loads without errors
    }

    @BeforeEach
    void setup() throws IOException {
        // Create test user entity
        test_user = new Users();
        test_user.setId(110L);
        test_user.setUsername("test user");
        test_user.setPassword("password");
        test_user.setEmail("test@test.com");

        // Create test image face entity
        test_image_face = new ImageFace();
        test_image_face.setId(99L);
        test_image_face.setFront("test_front_aspect.png");

        // Create test artifact entity
        artifact = new Artifact();
        artifact.setId(68L);
        artifact.setName("test product name");
        artifact.setDescription("test Description");
        artifact.setModel("test_model");
        artifact.setUser(test_user);
//        artifact.setImage_faces(test_image_face);
        artifact.setStatus(true);

        // Set bidirectional relationship
        test_image_face.setArtifact(artifact);

        // Load expected JSON files
        expected_product_json = mapper.readTree(getClass().getResource("/expected-artifact.json"));
        expected_user_json = mapper.readTree(getClass().getResource("/expected-users.json"));

        // Mock KafkaTemplate behavior
        when(kafkaTemplate.send(anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    @DisplayName("Test Kafka Producer Service")
    public void testKafkaProducerService() {
        // Gather data to send
        String topicName = "ml_streaming";
        JsonNode payload = jsonPayloadCreatorService.createJsonNode(artifact);

        // Publish message
        kafkaArtifactService.sendMessage(topicName, payload);

        // Verify that KafkaTemplate.send was called
        verify(kafkaTemplate).send(anyString(), any());
    }
}
