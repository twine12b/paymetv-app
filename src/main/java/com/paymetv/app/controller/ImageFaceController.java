package com.paymetv.app.controller;

import com.paymetv.app.domain.Artifact;
import com.paymetv.app.domain.ImageFace;
import com.paymetv.app.service.ImageFaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for ImageFace operations.
 *
 * Provides full CRUD operations for ImageFace entities:
 * - CREATE: POST /api/imageface/create
 * - READ (single): GET /api/imageface/{id}
 * - READ (all): GET /api/imageface/all
 * - UPDATE: PUT /api/imageface/update/{id}
 * - DELETE: DELETE /api/imageface/delete/{id}
 */
@RestController
@RequestMapping("/api/imageface")
@Tag(name = "ImageFace", description = "Full CRUD endpoints for managing image faces for artifacts")
public class ImageFaceController {

    @Autowired
    private ImageFaceService imageFaceService;

    public ImageFaceController(ImageFaceService imageFaceService) {
        this.imageFaceService = imageFaceService;
    }

    /**
     * Retrieves an ImageFace by ID.
     *
     * @param id the ID of the ImageFace to retrieve
     * @return the ImageFace entity
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get ImageFace by ID",
            description = "Retrieves a single ImageFace entity by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ImageFace found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageFace.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ImageFace not found"
            )
    })
    public ResponseEntity<ImageFace> getImageFace(
            @Parameter(description = "ID of the ImageFace to retrieve", required = true)
            @PathVariable Long id
    ) {
        ImageFace imageFace = imageFaceService.findById(id);
        return ResponseEntity.ok(imageFace);
    }

    /**
     * Retrieves all ImageFace entities.
     *
     * @return list of all ImageFace entities
     */
    @GetMapping("/all")
    @Operation(
            summary = "Get all ImageFaces",
            description = "Retrieves all ImageFace entities from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of ImageFaces retrieved successfully"
    )
    public ResponseEntity<List<ImageFace>> getAllImageFaces() {
        List<ImageFace> imageFaces = imageFaceService.findAll();
        return ResponseEntity.ok(imageFaces);
    }

    /**
     * Creates a new ImageFace for an Artifact.
     *
     * @param request containing the artifact and imageFace data
     * @return the created ImageFace with generated ID
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create an ImageFace",
            description = "Accepts an ImageFace and Artifact, validates them, and creates a new ImageFace entity"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ImageFace created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageFace.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or missing required fields",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ImageFace> createImageFace(
            @Parameter(description = "Request containing ImageFace and Artifact data", required = true)
            @RequestBody CreateImageFaceRequest request
    ) {
        ImageFace createdImageFace = imageFaceService.createImageFace(request.getImageFace(), request.getArtifact());
        return ResponseEntity.status(HttpStatus.OK).body(createdImageFace);
    }

    /**
     * Updates an existing ImageFace.
     *
     * @param id the ID of the ImageFace to update
     * @param request containing the updated ImageFace and Artifact data
     * @return the updated ImageFace entity
     */
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update an ImageFace",
            description = "Updates an existing ImageFace entity with new data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ImageFace updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageFace.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ImageFace not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or missing required fields"
            )
    })
    public ResponseEntity<ImageFace> updateImageFace(
            @Parameter(description = "ID of the ImageFace to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Request containing updated ImageFace data", required = true)
            @RequestBody CreateImageFaceRequest request
    ) {
        ImageFace updatedImageFace = imageFaceService.updateImageFace(id, request.getImageFace());
        return ResponseEntity.ok(updatedImageFace);
    }

    /**
     * Deletes an ImageFace by ID.
     *
     * @param id the ID of the ImageFace to delete
     * @return no content response
     */
    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete ImageFace",
            description = "Deletes an ImageFace entity by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "ImageFace deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ImageFace not found"
            )
    })
    public ResponseEntity<Void> deleteImageFace(
            @Parameter(description = "ID of the ImageFace to delete", required = true)
            @PathVariable Long id
    ) {
        imageFaceService.deleteImageFace(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Request DTO for creating an ImageFace.
     */
    public static class CreateImageFaceRequest {
        private ImageFace imageFace;
        private Artifact artifact;

        public CreateImageFaceRequest() {
        }

        public CreateImageFaceRequest(ImageFace imageFace, Artifact artifact) {
            this.imageFace = imageFace;
            this.artifact = artifact;
        }

        public ImageFace getImageFace() {
            return imageFace;
        }

        public void setImageFace(ImageFace imageFace) {
            this.imageFace = imageFace;
        }

        public Artifact getArtifact() {
            return artifact;
        }

        public void setArtifact(Artifact artifact) {
            this.artifact = artifact;
        }
    }
}
