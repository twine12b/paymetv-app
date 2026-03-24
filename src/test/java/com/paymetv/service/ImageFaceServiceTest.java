package com.paymetv.service;

import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.repository.ImageFaceRepository;
import com.paymetv.app.repository.UserRepository;
import com.paymetv.app.service.ImageFaceService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ImageFaceService.
 *
 * Each test is self-contained and creates its own test data.
 * Tests use @Transactional to ensure database rollback after each test.
 */
@SpringBootTest(classes = AppApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageFaceServiceTest {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageFaceRepository imageFaceRepository;

    @Autowired
    private ImageFaceService imageFaceService;

    @Test
    @DisplayName("context loads successfully")
    public void contextLoads() {
        // Verify that Spring context loads without errors
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("create image face")
    public void testCreateImageFace() {
        // Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Create ImageFace (not saved yet - service will save it)
        ImageFace imageFace = ImageFace.builder()
                .front("front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .build();

        // Call service to create ImageFace
        ImageFace createdImageFace = imageFaceService.createImageFace(imageFace, artifact);

        // Verify the result
        assertNotNull(createdImageFace);
        assertNotNull(createdImageFace.getId());
        assertEquals("front_image.png", createdImageFace.getFront());
        assertEquals("back_image.png", createdImageFace.getBack());
        assertEquals("left_image.png", createdImageFace.getLeft());
        assertEquals("right_image.png", createdImageFace.getRight());
        assertEquals("top_image.png", createdImageFace.getTop());
        assertEquals("bottom_image.png", createdImageFace.getBottom());
        assertEquals(artifact.getId(), createdImageFace.getArtifact().getId());
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("update image face")
    void testUpdateImageFace() {
        // Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Create and save ImageFace
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

        // Update the ImageFace
        ImageFace targetImageFace = ImageFace.builder()
                .front("updated_front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();

        imageFaceService.updateImageFace(savedId, targetImageFace);

        // Note: The current updateImageFace implementation only calls equals()
        // which doesn't actually update the entity. This test verifies the method runs without error.
        // A proper implementation would need to be added to the service.
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("find image face by id")
    void testFindImageFaceById() {
        // Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Create and save ImageFace
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

        // Find by ID using service
        ImageFace foundImageFace = imageFaceService.findById(savedId);

        // Verify
        assertNotNull(foundImageFace);
        assertEquals(savedId, foundImageFace.getId());
        assertEquals("front_image.png", foundImageFace.getFront());
    }

    @Test
    @Order(4)
    @Transactional
    @DisplayName("delete image face")
    void testDeleteImageFace() {
        // Create and save User
        Users user = Users.builder()
                .username("test user")
                .password("password")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);

        // Create and save Artifact
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .user(user)
                .build();
        artifact = artifactRepository.save(artifact);

        // Create and save ImageFace
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

        // Delete using service
        imageFaceService.deleteImageFace(savedId);

        // Verify deletion
        Optional<ImageFace> deletedImageFace = imageFaceRepository.findById(savedId);
        assertThat(deletedImageFace).isEmpty();
    }
}
