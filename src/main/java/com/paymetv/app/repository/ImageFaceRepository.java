package com.paymetv.app.repository;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories(basePackages = "com.paymetv.app.repository")
public interface ImageFaceRepository extends JpaRepository<ImageFace, Long> {
    Optional <ImageFace> findByFront(String image);

    Optional <ImageFace> findByArtifact(Artifact artifact);
}
