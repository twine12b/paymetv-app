package com.paymetv.repository;

import com.paymetv.app.AppApplication;
import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.repository.ArtifactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = AppApplication.class)
@DataJpaTest
@Tag("ArtifactRepositoryTests")
class ArtifactRepositoryTest {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Artifact widget;
    private Artifact gadget;
    private ImageFace imageFaces;

    @BeforeEach
    void setUp() {
        widget = new Artifact();
        widget.setName("Widget");
        widget.setDescription("A useful widget");
        widget.setStatus(true);
        entityManager.persistAndFlush(widget);

        gadget = new Artifact();
        gadget.setName("Gadget");
        gadget.setDescription("A handy gadget");
        gadget.setModel("Some model after successful training");
        gadget.setImage_faces(imageFaces);
        gadget.setStatus(false);
        entityManager.persistAndFlush(gadget);
    }

    @Test
    @DisplayName("findAll() returns every persisted product")
    void testFindAllProducts() {
        List<Artifact> products = artifactRepository.findAll();

        assertThat(products)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @DisplayName("save() persists a new product and assigns a generated id")
    void testSaveProduct() {
        Artifact newProduct = new Artifact();
        newProduct.setName("Thingamajig");
        newProduct.setDescription("Some thingamajig");
        newProduct.setStatus(true);

        Artifact saved = artifactRepository.save(newProduct);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getName()).isEqualTo("Thingamajig");
        assertThat(saved.getStatus()).isTrue();
    }

    @Test
    @DisplayName("findById() returns the correct product when it exists")
    void testFindById() {
        Optional<Artifact> found = artifactRepository.findById(widget.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Widget");
        assertThat(found.get().getDescription()).isEqualTo("A useful widget");
    }

//    @Test
//    @DisplayName("findById() returns empty when the id does not exist")
//    void testFindByIdNotFound() {
//        Optional<Artifact> found = artifactRepository.findById(Integer.MAX_VALUE);
//
//        assertThat(found).isNotPresent();
//    }

//    @Test
//    @DisplayName("findByName() returns products matching the given name")
//    void testFindByName() {
//        List<Artifact> results = artifactRepository.findByName("Widget");
//
//        assertThat(results)
//                .hasSize(1)
//                .extracting(Artifact::getName)
//                .containsExactly("Widget");
//    }
//
//    @Test
//    @DisplayName("findByStatus() returns only products with the requested status")
//    void testFindByStatus() {
//        List<Artifact> activeArtifacts = artifactRepository.findByStatus(true);
//
//        assertThat(activeArtifacts)
//                .isNotEmpty()
//                .allMatch(p -> Boolean.TRUE.equals(p.getStatus()));
//    }

    @Test
    @DisplayName("delete() removes the product so it no longer appears in findAll()")
    void testDeleteProduct() {
        artifactRepository.delete(widget);
        entityManager.flush();

        List<Artifact> remaining = artifactRepository.findAll();

        assertThat(remaining)
                .hasSize(1)
                .extracting(Artifact::getName)
                .containsExactly("Gadget");
    }

//    @Test
//    @DisplayName("save() on an existing product updates its fields")
//    void testUpdateProduct() {
//        widget.setDescription("Updated description");
//        artifactRepository.save(widget);
//        entityManager.flush();
//        entityManager.clear();
//
//        Artifact reloaded = artifactRepository.findById(widget.getId()).orElseThrow();
//        assertThat(reloaded.getDescription()).isEqualTo("Updated description");
//    }
}

