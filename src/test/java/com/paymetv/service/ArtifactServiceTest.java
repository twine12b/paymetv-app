package com.paymetv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.service.JsonPayloadCreatorService;
import com.paymetv.app.service.ProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * Test class for Artifact-related services.
 *
 * Uses @SpringBootTest to load the full application context.
 * Mocks ProducerService to avoid requiring a real Kafka broker.
 */
@SpringBootTest(classes = AppApplication.class)
public class ArtifactServiceTest {

    @MockitoBean
    private ProducerService producerService;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private JsonPayloadCreatorService jsonPayloadCreatorService;

    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_artifact_json;
    private JsonNode expected_user_json;
    private JsonNode expected_imageface_json;

    private Artifact test_artifact;
    private Users test_user;
    private ImageFace test_image_face;

    /**
     * Configure test properties to disable Kafka auto-configuration
     * since we're mocking the ProducerService.
     */
    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        // Disable Kafka auto-configuration for this test
        registry.add("spring.autoconfigure.exclude",
                () -> "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
    }

    @BeforeEach
    public void setup() throws IOException {
        // Create test entities
        test_user = new Users(110L, "test user", "password", "test@test.com");

        test_artifact = new Artifact(68L, "test product name", "test Description",
                "some_test_model stored as string", test_user, true);

        test_image_face = new ImageFace(99L, "test_front_aspect.png", "test_back_aspect.png",
                "test_left_aspect.png", "test_right_aspect.png", "test_top_aspect.png",
                "test_bottom_aspect.png", test_artifact);


        // Set the artifact reference in image_face (bidirectional relationship)
        test_image_face.setArtifact(test_artifact);

        // Load expected JSON files
        expected_user_json = mapper.readTree(getClass().getResource("/expected-users.json"));
        expected_artifact_json = mapper.readTree(getClass().getResource("/expected-artifact.json"));
        expected_imageface_json = mapper.readTree(getClass().getResource("/expected-image-face.json"));

        // Mock ProducerService behavior
        doNothing().when(producerService).sendMessage(anyString(), any());
    }

    @Test
    @DisplayName("Context loads successfully")
    void jsonProducerload() {  }

    @Test
    @DisplayName("Create a Json file from a Artifact object and a filename [string value]")
    void jsonProducer() {
        JsonNode payload = jsonPayloadCreatorService.createJsonNode(test_artifact);
        String topic = "file-uploaded.json";

        System.out.println("Sending message: " + payload.toString());
        producerService.sendMessage(topic, test_artifact);
    }

    @Test
    @DisplayName("getAllArtifactFromDatabase_inJson")
    void jsonConsumer() {
        // Given data call tp database
        List<Artifact> allArtifacts = artifactRepository.findAll();

        System.out.println("Found " + allArtifacts.size() + " artifacts");
        assertNotNull(allArtifacts);

    }

}
