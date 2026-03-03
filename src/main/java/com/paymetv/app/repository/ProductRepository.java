package com.paymetv.app.repository;

import com.paymetv.app.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>All standard CRUD operations are inherited from {@link JpaRepository}.
 * Custom query methods can be added here using Spring Data's derived-query
 * naming convention or with explicit {@code @Query} annotations.</p>
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Derived query: SELECT p FROM Product p WHERE p.name = :name
    List<Product> findByName(String name);

    // Derived query: SELECT p FROM Product p WHERE p.status = :status
    List<Product> findByStatus(Boolean status);

//    Optional<Product> findById(Integer id);
}

