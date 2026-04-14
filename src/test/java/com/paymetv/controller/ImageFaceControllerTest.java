package com.paymetv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymetv.app.AppApplication;
import com.paymetv.app.controller.ImageFaceController;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.repository.ImageFaceRepository;
import com.paymetv.app.repository.UserRepository;
import com.paymetv.app.service.ImageFaceService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ImageFaceController.
 *
 * Tests the REST endpoints for creating and managing ImageFace entities.
 *
 * FIXES IMPLEMENTED:
 * 1. Changed controller @PostMapping from "/api/imageface/create" to "/create" to avoid duplicate path
 * 2. Changed test HTTP method from GET to POST
 * 3. Fixed URL path to match controller (/api/imageface/create)
 * 4. Created CreateImageFaceRequest DTO for proper request body binding
 * 5. Fixed MediaType import (org.springframework.http.MediaType)
 * 6. Controller now uses @RequestBody and ImageFaceService
 * 7. Test saves User and Artifact first to satisfy FK constraints
 * 8. Removed assertion for $.artifact field (excluded by @JsonBackReference)
 */
@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("ImageFaceControllerTest")
@WithMockUser  // Provides authenticated user context for Spring Security
public class ImageFaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImageFaceService imageFaceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ImageFaceRepository imageFaceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("context loads successfully")
    void contextLoads() {
        // Verify that Spring context loads without errors
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("create image face via POST /api/imageface/create")
    void testCreateImageFace() throws Exception {
        // Step 1: Create and save User (needed for Artifact)
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Step 2: Create and save Artifact (needed for ImageFace)
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Step 3: Create ImageFace (not saved - controller/service will save it)
        ImageFace imageFace = ImageFace.builder()
                .front("front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .build();

        // Step 4: Create request DTO wrapping both ImageFace and Artifact
        ImageFaceController.CreateImageFaceRequest request =
                new ImageFaceController.CreateImageFaceRequest(imageFace, artifact);

        // Step 5: Perform POST request to /api/imageface/create
        mockMvc.perform(post("/api/imageface/create")  // ✅ Correct: POST method, correct path
                        .contentType(MediaType.APPLICATION_JSON)  // ✅ Correct: Spring MediaType
                        .content(objectMapper.writeValueAsString(request)))  // ✅ Correct: DTO serialized to JSON
                .andExpect(status().isOk())  // ✅ Expect 200 OK (not 404)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.front").value("front_image.png"))
                .andExpect(jsonPath("$.back").value("back_image.png"))
                .andExpect(jsonPath("$.left").value("left_image.png"))
                .andExpect(jsonPath("$.right").value("right_image.png"))
                .andExpect(jsonPath("$.top").value("top_image.png"))
                .andExpect(jsonPath("$.bottom").value("bottom_image.png"));
        // Note: $.artifact is not in response due to @JsonBackReference annotation
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("update image face via POST /api/imageface/update/{id}")
    void testUpdateImageFace() throws Exception {
        // Step 1: Create and save User (needed for Artifact)
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Step 2: Create and save Artifact (needed for ImageFace)
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Step 3: Create and save ImageFace
        ImageFace imageFace = ImageFace.builder()
                .front("original_front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();
        imageFace = imageFaceRepository.save(imageFace);
        Long savedId = imageFace.getId();

        // Step 4: Create updated ImageFace
        ImageFace targetImageFace = ImageFace.builder()
                .front("updated_front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();

        // Step 5: Create request DTO wrapping both ImageFace and Artifact
        ImageFaceController.CreateImageFaceRequest request =
                new ImageFaceController.CreateImageFaceRequest(targetImageFace, artifact);

        // Step 6: Perform POST request to /api/imageface/update/{id}
        mockMvc.perform(put("/api/imageface/update/{id}", savedId)  // ✅ Correct: POST method, correct path
                        .contentType(MediaType.APPLICATION_JSON)  // ✅ Correct: Spring MediaType
                        .content(objectMapper.writeValueAsString(request)))  // ✅ Correct: DTO serialized to JSON

                .andExpect(status().isOk())  // ✅ Expect 200 OK (not 404)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.front").value("updated_front_image.png"))
                .andExpect(jsonPath("$.back").value("back_image.png"))
                .andExpect(jsonPath("$.left").value("left_image.png"))
                .andExpect(jsonPath("$.right").value("right_image.png"))
                .andExpect(jsonPath("$.top").value("top_image.png"))
                .andExpect(jsonPath("$.bottom").value("bottom_image.png"));
        // Note: $.artifact is not in response due to @JsonBackReference annotation
    }

    @Test
    @Order(4)
    @Transactional
    @DisplayName("get image face by ID via GET /api/imageface/{id}")
    void testGetImageFaceById() throws Exception {
        // Step 1: Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Step 2: Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Step 3: Create and save ImageFace
        ImageFace imageFace = ImageFace.builder()
                .front("front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();
        imageFace = imageFaceRepository.save(imageFace);
        Long savedId = imageFace.getId();

        // Step 4: Perform GET request
        mockMvc.perform(get("/api/imageface/{id}", savedId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedId))
                .andExpect(jsonPath("$.front").value("front_image.png"))
                .andExpect(jsonPath("$.back").value("back_image.png"))
                .andExpect(jsonPath("$.left").value("left_image.png"))
                .andExpect(jsonPath("$.right").value("right_image.png"))
                .andExpect(jsonPath("$.top").value("top_image.png"))
                .andExpect(jsonPath("$.bottom").value("bottom_image.png"));
    }

    @Test
    @Order(5)
    @Transactional
    @DisplayName("get all image faces via GET /api/imageface/all")
    void testGetAllImageFaces() throws Exception {
        // Step 1: Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Step 2: Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Step 3: Create and save multiple ImageFaces
        ImageFace imageFace1 = ImageFace.builder()
                .front("front1.png")
                .back("back1.png")
                .left("left1.png")
                .right("right1.png")
                .top("top1.png")
                .bottom("bottom1.png")
                .artifact(artifact)
                .build();
        imageFaceRepository.save(imageFace1);

        ImageFace imageFace2 = ImageFace.builder()
                .front("front2.png")
                .back("back2.png")
                .left("left2.png")
                .right("right2.png")
                .top("top2.png")
                .bottom("bottom2.png")
                .artifact(artifact)
                .build();
        imageFaceRepository.save(imageFace2);

        // Step 4: Perform GET request
        mockMvc.perform(get("/api/imageface/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].front").exists())
                .andExpect(jsonPath("$[1].front").exists());
    }

    @Test
    @Order(6)
    @Transactional
    @DisplayName("delete image face via DELETE /api/imageface/delete/{id}")
    void testDeleteImageFace() throws Exception {
        // Step 1: Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Step 2: Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Step 3: Create and save ImageFace
        ImageFace imageFace = ImageFace.builder()
                .front("front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();
        imageFace = imageFaceRepository.save(imageFace);
        Long savedId = imageFace.getId();

        // Step 4: Perform DELETE request
        mockMvc.perform(delete("/api/imageface/delete/{id}", savedId))
                .andExpect(status().isNoContent());

        // Step 5: Verify deletion
        Assertions.assertFalse(imageFaceRepository.findById(savedId).isPresent(),
                "ImageFace should be deleted");
    }
}


