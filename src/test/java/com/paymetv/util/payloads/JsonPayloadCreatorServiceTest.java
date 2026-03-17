package com.paymetv.util.payloads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.UserRepository;
import com.paymetv.app.service.JsonPayloadCreatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

//@ContextConfiguration(classes = AppApplication.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
class JsonPayloadCreatorServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonPayloadCreatorService jsonPayloadCreatorService;

    private Artifact artifact;
    private Users test_user;
    private ImageFace test_image_face;

    ObjectMapper mapper = new ObjectMapper();
    private JsonNode expected_artifact_json;
    private JsonNode expected_user_json;

    @Test
    void jsonImagePayload() throws IllegalStateException {
    }

    @BeforeEach
    void setup() throws IOException {
        test_user = new Users();
        test_user.setId(110L);
        test_user.setUsername("test user");
        test_user.setPassword("password");
        test_user.setEmail("test@test.com");

        test_image_face = new ImageFace(99L, "test_front_aspect.png", artifact);

        artifact = new Artifact();
        artifact.setId(68L);
        artifact.setName("test artifact name");
        artifact.setDescription("test Description");
        artifact.setUser(test_user);
        artifact.setModel("some_test_model stored as string");
        artifact.setImage_faces(test_image_face);
        artifact.setStatus(true);

        test_image_face.setArtifact(artifact);

        expected_artifact_json = mapper.readTree(getClass().getResource("/expected-artifact.json"));
        expected_user_json = mapper.readTree(getClass().getResource("/expected-users.json"));
    }

    @Test
    @DisplayName("create a Json file from a Artifact object and a filename [string value]")
    void createArtifactJsonFile() throws JsonProcessingException, InterruptedException {

        String filename = "artifact_test.json";
        Object object = artifact;

        JsonNode actual = jsonPayloadCreatorService.createJsonNode(object);

        assertNotNull(actual);
        assertEquals(prettyPrintJsonString(expected_artifact_json), prettyPrintJsonString(actual));
    }

    @Test
    @DisplayName("create a Json file from a Users object and a filename [string value]")
    void createUsersJsonFile() throws JsonProcessingException {
        String filename = "user_test.json";
        Object object = test_user;

        JsonNode actual = jsonPayloadCreatorService.createJsonNode(object);
        assertNotNull(actual);
        assertEquals(prettyPrintJsonString(expected_user_json), prettyPrintJsonString(actual));
    }

    private String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }
}
