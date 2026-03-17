package com.paymetv.service;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

//@ContextConfiguration(classes = AppApplication.class)
@SpringBootApplication(scanBasePackages = "com.paymetv.app")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
public class ArtifactServiceTest {

//    @Autowired
//    private MockMvc mockMvc;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    JsonPayloadCreatorService jsonPayloadCreatorService;

    ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_artifact_json;
    private JsonNode expected_user_json;
    private JsonNode expected_imageface_json;

    private Artifact test_artifact;
    private Users test_user;
    private ImageFace test_image_face;

    @BeforeEach
    public void setup() throws IOException {
        test_user = new Users(110L, "test user", "password", "test@test.com");
        test_artifact = new Artifact(68L, "test product name", "test Description", "some_test_model stored as string", test_image_face,test_user, true);
        test_image_face = new ImageFace(99L, "test_front_aspect.png", test_artifact);


        expected_user_json = mapper.readTree(getClass().getResource("/expected-users.json"));
        expected_artifact_json = mapper.readTree(getClass().getResource("/expected-artifact.json"));
        expected_imageface_json = mapper.readTree(getClass().getResource("/expected-image-face.json"));
    }

    @Test
    @DisplayName("Context loads successfully")
    void jsonProducerload() {  }

    @Test
    @DisplayName("Context loads successfully")
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
