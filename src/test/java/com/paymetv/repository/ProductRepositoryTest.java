package com.paymetv.repository;

import com.paymetv.app.domain.Product;
import com.paymetv.app.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository-layer tests for {@link ProductRepository}.
 *
 * <p>{@code @DataJpaTest} spins up only the JPA slice (Hibernate + H2) and
 * wraps every test method in a transaction that is rolled back on completion,
 * so tests are fully isolated from one another.</p>
 */
@DataJpaTest
@Tag("ProductRepositoryTests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    /** Provides direct JPA access for setting up test fixtures. */
    @Autowired
    private TestEntityManager entityManager;

    // -------------------------------------------------------------------------
    // Test fixtures – persisted before each test, rolled back afterwards
    // -------------------------------------------------------------------------

    private Product widget;
    private Product gadget;

    @BeforeEach
    void setUp() {
        widget = new Product();
        widget.setName("Widget");
        widget.setDescription("A useful widget");
        widget.setStatus(true);
        entityManager.persistAndFlush(widget);

        gadget = new Product();
        gadget.setName("Gadget");
        gadget.setDescription("A handy gadget");
        gadget.setStatus(false);
        entityManager.persistAndFlush(gadget);
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll() returns every persisted product")
    void testFindAllProducts() {
        List<Product> products = productRepository.findAll();

        assertThat(products)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @DisplayName("save() persists a new product and assigns a generated id")
    void testSaveProduct() {
        Product newProduct = new Product();
        newProduct.setName("Thingamajig");
        newProduct.setDescription("Some thingamajig");
        newProduct.setStatus(true);

        Product saved = productRepository.save(newProduct);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getName()).isEqualTo("Thingamajig");
        assertThat(saved.getStatus()).isTrue();
    }

    @Test
    @DisplayName("findById() returns the correct product when it exists")
    void testFindById() {
        Optional<Product> found = productRepository.findById(widget.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Widget");
        assertThat(found.get().getDescription()).isEqualTo("A useful widget");
    }

//    @Test
//    @DisplayName("findById() returns empty when the id does not exist")
//    void testFindByIdNotFound() {
//        Optional<Product> found = productRepository.findById(Integer.MAX_VALUE);
//
//        assertThat(found).isNotPresent();
//    }

    @Test
    @DisplayName("findByName() returns products matching the given name")
    void testFindByName() {
        List<Product> results = productRepository.findByName("Widget");

        assertThat(results)
                .hasSize(1)
                .extracting(Product::getName)
                .containsExactly("Widget");
    }

    @Test
    @DisplayName("findByStatus() returns only products with the requested status")
    void testFindByStatus() {
        List<Product> activeProducts = productRepository.findByStatus(true);

        assertThat(activeProducts)
                .isNotEmpty()
                .allMatch(p -> Boolean.TRUE.equals(p.getStatus()));
    }

    @Test
    @DisplayName("delete() removes the product so it no longer appears in findAll()")
    void testDeleteProduct() {
        productRepository.delete(widget);
        entityManager.flush();

        List<Product> remaining = productRepository.findAll();

        assertThat(remaining)
                .hasSize(1)
                .extracting(Product::getName)
                .containsExactly("Gadget");
    }

//    @Test
//    @DisplayName("save() on an existing product updates its fields")
//    void testUpdateProduct() {
//        widget.setDescription("Updated description");
//        productRepository.save(widget);
//        entityManager.flush();
//        entityManager.clear();
//
//        Product reloaded = productRepository.findById(widget.getId()).orElseThrow();
//        assertThat(reloaded.getDescription()).isEqualTo("Updated description");
//    }
}

