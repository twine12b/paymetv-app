package com.paymetv.repository;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.repository.ImageFaceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ImageFaceRepository.
 *
 * Each test is self-contained and creates its own test data.
 * Tests run in isolated transactions that are rolled back after each test.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("ImageRepositoryTest")
public class ImageFaceTest {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ImageFaceRepository imageFaceRepository;

    private Artifact artifact;
    private ImageFace imageFace;

    @Test
    @DisplayName("context loads the ImageFaceRepository bean")
    void testContextLoads() {
        // Verify that Spring context loads successfully
    }

    @Test
    @Order(1)
    @DisplayName("save image face and find by id")
    void testSaveAndFindById() {
        dataSetup();

        // Verify ID was generated
        assertNotNull(imageFace.getId());
        assertTrue(imageFace.getId() > 0);
    }

    @Test
    @Order(2)
    @DisplayName("find image face by id")
    void testFindById() {
        dataSetup();

        Long savedId = imageFace.getId();

        // Find by the auto-generated ID
        ImageFace foundOptional = imageFaceRepository.getReferenceById(savedId);

        // Assert the entity was found
        assertNotNull(foundOptional);

        assertNotNull(foundOptional);
        assertEquals(savedId, foundOptional.getId());
        assertEquals("front_image.png", foundOptional.getFront());
        assertEquals("back_image.png", foundOptional.getBack());
        assertEquals("left_image.png", foundOptional.getLeft());
        assertEquals("right_image.png", foundOptional.getRight());
        assertEquals("top_image.png", foundOptional.getTop());
        assertEquals("bottom_image.png", foundOptional.getBottom());
        assertEquals(artifact.getId(), foundOptional.getArtifact().getId());
    }

    @Test
    @Order(3)
    @DisplayName("find image face by front field")
    void testFindByFront() {
        // Create and save parent Artifact
        dataSetup();
        imageFace.setFront("unique_front_image.png");

        // Find by front field
        Optional<ImageFace> foundOptional = imageFaceRepository.findByFront("unique_front_image.png");

        // Assert
        assertTrue(foundOptional.isPresent(), "ImageFace should be found by front field");
        ImageFace foundImageFace = foundOptional.get();
        assertNotNull(foundImageFace);
        assertEquals("unique_front_image.png", foundImageFace.getFront());
        assertEquals(imageFace.getId(), foundImageFace.getId());
    }

    @Test
    @Order(4)
    @DisplayName("find image face by artifact")
    void testFindByArtifact() {
        dataSetup();

        // Find by artifact
        Optional<ImageFace> foundOptional = imageFaceRepository.findByArtifact(artifact);

        // Assert
        assertTrue(foundOptional.isPresent(), "ImageFace should be found by artifact");
        ImageFace foundImageFace = foundOptional.get();
        assertNotNull(foundImageFace);
        assertEquals(imageFace.getId(), foundImageFace.getId());
        assertEquals(artifact.getId(), foundImageFace.getArtifact().getId());
    }

    @Test
    @Order(5)
    @DisplayName("update an existing image face")
    void testUpdateImageFace() {
        dataSetup();

        Long savedId = imageFace.getId();

        // Update the front field
        imageFace.setFront("updated_front_image.png");
        imageFaceRepository.save(imageFace);

        // Verify the update
        Optional<ImageFace> updatedOptional = imageFaceRepository.findById(savedId);
        assertTrue(updatedOptional.isPresent());
        assertThat(updatedOptional.get().getFront()).isEqualTo("updated_front_image.png");
    }

    @Test
    @Order(6)
    @DisplayName("delete image face")
    void testDeleteImageFace() {
        dataSetup();

        Long savedId = imageFace.getId();
        Long artifactId = artifact.getId();

        // Delete the ImageFace
        imageFaceRepository.deleteById(savedId);

        // Verify deletion
        assertTrue(imageFaceRepository.findById(savedId).isEmpty(), "ImageFace should be deleted");

        // Artifact should still exist (no cascade delete)
        assertTrue(artifactRepository.findById(artifactId).isPresent(), "Artifact should still exist");
    }

    private void dataSetup() {
        // Create and save parent Artifact
        this.artifact = Artifact.builder()
                .name("test artifact for delete")
                .description("test description")
                .model("test model")
                .status(true)
                .build();
        artifact = artifactRepository.save(artifact);

        // Create and save ImageFace
        this.imageFace = ImageFace.builder()
                .front("front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();
        imageFace = imageFaceRepository.save(imageFace);
    }

}
