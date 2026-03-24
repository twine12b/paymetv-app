package com.paymetv.app.service;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.repository.ArtifactRepository;
import com.paymetv.app.repository.ImageFaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageFaceService {

    private final ImageFaceRepository imageFaceRepository;
    private final ArtifactRepository artifactRepository;
    private final JsonPayloadCreatorService jsonPayloadCreatorService;

    public ImageFaceService(ImageFaceRepository imageFaceRepository, ArtifactRepository artifactRepository, JsonPayloadCreatorService jsonPayloadCreatorService) {
        this.imageFaceRepository = imageFaceRepository;
        this.artifactRepository = artifactRepository;
        this.jsonPayloadCreatorService = jsonPayloadCreatorService;
    }

    @Transactional
    public ImageFace createImageFace(ImageFace imageFace, Artifact artifact) {
        imageFace.setArtifact(artifact);
        imageFaceRepository.save(imageFace);
        return imageFaceRepository.getReferenceById(imageFace.getId());
    }

    @Transactional
    public void updateImageFace(Long id, ImageFace targetImageFace) {
        imageFaceRepository.getReferenceById(id).equals(targetImageFace);
    }

    @Transactional
    public ImageFace findById(long id) {
        return imageFaceRepository.getReferenceById(id);
    }

    public void deleteImageFace(long l) {
        imageFaceRepository.deleteById(l);
    }
}
