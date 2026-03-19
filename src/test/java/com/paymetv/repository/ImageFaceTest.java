package com.paymetv.repository;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.repository.ImageFaceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("ImageRepositoryTest")
public class ImageFaceTest {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ImageFaceRepository imageFaceRepository;

    @Test
    @DisplayName("context loads the ImageFaceRepository bean")
    void testContextLoads() { }

    @Test
    @Order(1)
    @DisplayName("save image face and find by id")
    void testSaveAndFindById() {
        // Create and save the parent Artifact first
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .build();

        artifact = artifactRepository.save(artifact);

        ImageFace imageFace = ImageFace.builder()
                .front("front_image.png")
                .artifact(artifact)
                .build();

        imageFace = imageFaceRepository.save(imageFace);

        Assertions.assertNotNull(imageFace.getId());
        Assertions.assertTrue(imageFace.getId() > 0);
    }
}

