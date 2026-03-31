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

    public static Object getImageFace() {
        return new String("This is working");
    }

    public String giveMeString() {
        return "Hello World!";
    }

    @Transactional
    public ImageFace createImageFace(ImageFace imageFace, Artifact artifact) {
        imageFace.setArtifact(artifact);
        imageFaceRepository.save(imageFace);
        return imageFaceRepository.getReferenceById(imageFace.getId());
    }

    @Transactional
    public ImageFace updateImageFace(Long id, ImageFace targetImageFace) {
        ImageFace existingImageFace = imageFaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ImageFace not found with id: " + id));

        // Update fields
        existingImageFace.setFront(targetImageFace.getFront());
        existingImageFace.setBack(targetImageFace.getBack());
        existingImageFace.setLeft(targetImageFace.getLeft());
        existingImageFace.setRight(targetImageFace.getRight());
        existingImageFace.setTop(targetImageFace.getTop());
        existingImageFace.setBottom(targetImageFace.getBottom());
        if (targetImageFace.getArtifact() != null) {
            existingImageFace.setArtifact(targetImageFace.getArtifact());
        }

        imageFaceRepository.save(existingImageFace);
        return existingImageFace;
    }

    /**
     * Finds an ImageFace by ID.
     *
     * @param id the ID to search for
     * @return the ImageFace entity
     * @throws RuntimeException if not found
     */
    public ImageFace findById(Long id) {
        return imageFaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ImageFace not found with id: " + id));
    }

    /**
     * Retrieves all ImageFace entities.
     *
     * @return list of all ImageFaces
     */
    public java.util.List<ImageFace> findAll() {
        return imageFaceRepository.findAll();
    }

    /**
     * Deletes an ImageFace by ID.
     *
     * @param id the ID of the ImageFace to delete
     * @throws RuntimeException if not found
     */
    @Transactional
    public void deleteImageFace(Long id) {
        if (!imageFaceRepository.existsById(id)) {
            throw new RuntimeException("ImageFace not found with id: " + id);
        }
        imageFaceRepository.deleteById(id);
    }
}
