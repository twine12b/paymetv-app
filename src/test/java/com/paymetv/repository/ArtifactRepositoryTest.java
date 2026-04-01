package com.paymetv.repository;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.repository.ArtifactRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@Tag("ArtifactRepositoryTests")
public class ArtifactRepositoryTest {
    @Autowired
    private ArtifactRepository artifactRepository;

    // Holds the artifact created by @BeforeEach for use in each test method.
    // Because @DataJpaTest wraps every test in its own transaction that rolls back
    // after the method completes, each test always starts with a clean database –
    // no rows ever accumulate across methods or test classes.
    private Artifact savedArtifact;

    @BeforeEach
    void setUp() {
        savedArtifact = artifactRepository.save(
                Artifact.builder()
                        .name("test artifact")
                        .description("test description")
                        .model("test model")
                        .status(true)
                        .build()
        );
    }

    @Test
    @DisplayName("context loads the ArtifactRepository bean")
    void testContextLoads() { }

    @Test
    @Order(1)
    @DisplayName("save artifact and find by id")
    void testSaveAndFindById() {
        // savedArtifact was already persisted by @BeforeEach inside this transaction.
        assertThat(savedArtifact.getId()).isGreaterThan(0);
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
        // Exactly one artifact with this name exists (the one from @BeforeEach).
        // Using orElseThrow() instead of get() gives a clearer failure message
        // if the Optional is unexpectedly empty.
        Artifact artifact = artifactRepository.findByName("test artifact").orElseThrow();
        assertThat(artifact.getName()).isEqualTo("test artifact");
    }

    @Test
    @Order(4)
    @DisplayName("find artifact by description")
    void testFindByDescription() {
        // Exactly one artifact with this description exists (the one from @BeforeEach).
        // findByDescription() returns Optional<Artifact>; calling orElseThrow() is
        // both safer and correct – no .stream().findAny() wrapper needed.
        Artifact artifact = artifactRepository.findByDescription("test description").orElseThrow();
        assertThat(artifact.getDescription()).isEqualTo("test description");
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

