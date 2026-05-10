package com.paymetv.app.repository;

import com.paymetv.app.domain.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Artifact} entities.
 *
 * <p>All standard CRUD operations are inherited from {@link JpaRepository}.
 * Custom query methods can be added here using Spring Data's derived-query
 * naming convention or with explicit {@code @Query} annotations.</p>
 */
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {
    Optional<Artifact> findByName(String testArtifact);

    Optional<Artifact> findByDescription(String testDescription);

    List<Artifact> findByStatus(boolean b);
}
