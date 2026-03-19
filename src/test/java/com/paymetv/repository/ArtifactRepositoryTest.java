package com.paymetv.repository;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.repository.ArtifactRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("ArtifactRepositoryTests")
class ArtifactRepositoryTest {
    @Autowired
    private ArtifactRepository artifactRepository;

    @Test
    @DisplayName("context loads the ArtifactRepository bean")
    void testContextLoads() { }

    @Test
    @Order(1)
    @Rollback(false)
    @DisplayName("save artifact and find by id")
    void testSaveAndFindById() {
        Artifact artifact = Artifact.builder()
                .name("test artifact")
                .description("test description")
                .model("test model")
                .status(true)
                .build();

        artifactRepository.save(artifact);
        assertThat(artifact.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("find all artifacts")
    void testFindAllArtifacts() {
        List<Artifact> artifacts = artifactRepository.findAll();
        assertThat(artifacts).isNotEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("find artifact by name")
    void testFindByName() {
        Artifact artifact = artifactRepository.findByName("test artifact").get();
        assertThat(artifact).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("Find artifact by description")
    void testFindByDescription() {
        Artifact artifact = artifactRepository.findByDescription("test description").get();
        assertThat(artifact).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("find artifacts by status")
    void testFindArtifactModel() {
        List<Artifact> artifacts = artifactRepository.findByStatus(true);
        assertThat(artifacts).isNotEmpty();
    }


//    @Autowired
//    private TestEntityManager entityManager;
//
//    private Artifact widget;
//    private Artifact gadget;
//    private ImageFace imageFaces;
//
//    @BeforeEach
//    void setUp() {
//        widget = new Artifact();
//        widget.setName("Widget");
//        widget.setDescription("A useful widget");
//        widget.setStatus(true);
//        entityManager.persistAndFlush(widget);
//
//        gadget = new Artifact();
//        gadget.setName("Gadget");
//        gadget.setDescription("A handy gadget");
//        gadget.setModel("Some model after successful training");
////        gadget.setImage_faces(imageFaces);
//        gadget.setStatus(false);
//        entityManager.persistAndFlush(gadget);
//    }
//
//    @Test
//    @DisplayName("findAll() returns every persisted product")
//    void testFindAllProducts() {
//        List<Artifact> products = artifactRepository.findAll();
//
//        assertThat(products)
//                .isNotEmpty()
//                .hasSize(2);
//    }
//
//    @Test
//    @DisplayName("save() persists a new product and assigns a generated id")
//    void testSaveProduct() {
//        Artifact newProduct = new Artifact();
//        newProduct.setName("Thingamajig");
//        newProduct.setDescription("Some thingamajig");
//        newProduct.setStatus(true);
//
//        Artifact saved = artifactRepository.save(newProduct);
//
//        assertThat(saved.getId()).isPositive();
//        assertThat(saved.getName()).isEqualTo("Thingamajig");
//        assertThat(saved.getStatus()).isTrue();
//    }
//
//    @Test
//    @DisplayName("findById() returns the correct product when it exists")
//    void testFindById() {
//        Optional<Artifact> found = artifactRepository.findById(widget.getId());
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getName()).isEqualTo("Widget");
//        assertThat(found.get().getDescription()).isEqualTo("A useful widget");
//    }
//
////    @Test
////    @DisplayName("findById() returns empty when the id does not exist")
////    void testFindByIdNotFound() {
////        Optional<Artifact> found = artifactRepository.findById(Integer.MAX_VALUE);
////
////        assertThat(found).isNotPresent();
////    }
//
////    @Test
////    @DisplayName("findByName() returns products matching the given name")
////    void testFindByName() {
////        List<Artifact> results = artifactRepository.findByName("Widget");
////
////        assertThat(results)
////                .hasSize(1)
////                .extracting(Artifact::getName)
////                .containsExactly("Widget");
////    }
////
////    @Test
////    @DisplayName("findByStatus() returns only products with the requested status")
////    void testFindByStatus() {
////        List<Artifact> activeArtifacts = artifactRepository.findByStatus(true);
////
////        assertThat(activeArtifacts)
////                .isNotEmpty()
////                .allMatch(p -> Boolean.TRUE.equals(p.getStatus()));
////    }
//
//    @Test
//    @DisplayName("delete() removes the product so it no longer appears in findAll()")
//    void testDeleteProduct() {
//        artifactRepository.delete(widget);
//        entityManager.flush();
//
//        List<Artifact> remaining = artifactRepository.findAll();
//
//        assertThat(remaining)
//                .hasSize(1)
//                .extracting(Artifact::getName)
//                .containsExactly("Gadget");
//    }
//
////    @Test
////    @DisplayName("save() on an existing product updates its fields")
////    void testUpdateProduct() {
////        widget.setDescription("Updated description");
////        artifactRepository.save(widget);
////        entityManager.flush();
////        entityManager.clear();
////
////        Artifact reloaded = artifactRepository.findById(widget.getId()).orElseThrow();
////        assertThat(reloaded.getDescription()).isEqualTo("Updated description");
////    }
}

