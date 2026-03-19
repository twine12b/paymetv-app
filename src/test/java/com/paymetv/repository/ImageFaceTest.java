package com.paymetv.repository;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.repository.ImageFaceRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("ImageRepositoryTest")
public class ImageFaceTest {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ImageFaceRepository imageFaceRepository;

    @Mock
    private Artifact artifact;

    @InjectMocks
    private ImageFace imageFace;

    @Test
    @DisplayName("context loads the ImageFaceRepository bean")
    void testContextLoads() { }

    @BeforeEach
    @DisplayName("set up test data")
    void setUp() {
        this.artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .build();

        artifactRepository.save(artifact);

        this.imageFace = ImageFace.builder()
                .front("front_image.png")
                .back("back_image.png")
                .left("left_image.png")
                .right("right_image.png")
                .top("top_image.png")
                .bottom("bottom_image.png")
                .artifact(artifact)
                .build();

        imageFaceRepository.save(imageFace);
    }

    @Test
    @Order(1)
    @Rollback(false)
    @DisplayName("save image face and find by id")
    void testSaveAndFindById() {

        assertNotNull(imageFace.getId());
        assertTrue(imageFace.getId() > 0);
    }

    @Test
    @Order(2)
    @DisplayName("find image face by id")
    void testFindById() {
        // id is 1L since we are using in memory db and it auto increments the id
        Long savedId = imageFaceRepository.findById(1L).get().getId();
        ImageFace foundImageFace = imageFaceRepository.findById(savedId).get();

        Artifact foundArtifact = artifactRepository.findById(1L).get();

        assertNotNull(foundImageFace);
        assertEquals(savedId, foundImageFace.getId());
        assertEquals(1L, foundArtifact.getId());  //USE 1L as another Unit test interferes with val
        assertEquals("front_image.png", foundImageFace.getFront());
        assertEquals("back_image.png", foundImageFace.getBack());
        assertEquals("left_image.png", foundImageFace.getLeft());
        assertEquals("right_image.png", foundImageFace.getRight());
        assertEquals("top_image.png", foundImageFace.getTop());
        assertEquals("bottom_image.png", foundImageFace.getBottom());

//        assertEquals(artifactId, foundImageFace.getArtifact().getId());
    }

    @Test
    @Order(3)
    @DisplayName("find image face by artifact id")
    void testFindByArtifactId() {
        ImageFace foundImageFace = imageFaceRepository.findByArtifact(artifact).get();

        assertNotNull(foundImageFace);
    }

    @Test
    @Order(4)
    @DisplayName("Update an existing image face")
    void testUpdateImageFace() {
        ImageFace foundImageFace = imageFaceRepository.findById(1L).get();
        foundImageFace.setFront("updated_front_image.png");
        imageFaceRepository.save(foundImageFace);

        assertThat(imageFaceRepository.findById(1L).get().getFront())
                .isEqualTo("updated_front_image.png");
    }

    @Test
    @Order(5)
    @DisplayName("delete image face")
    void testDeleteImageFace() {
        imageFaceRepository.deleteById(1L);
        assertTrue(imageFaceRepository.findById(1L).isEmpty());
        assertThat(artifactRepository.findById(1L).isEmpty());
        assertNotNull(artifactRepository.findById(1L));
    }

}

