package com.paymetv.app.controller;

import com.paymetv.app.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for file upload operations.
 *
 * Exposes a single POST endpoint at {@code /api/files/upload} that accepts a
 * multipart file, delegates validation and persistence to {@link FileUploadService},
 * and returns a JSON response describing the stored file.
 *
 * @author PayMeTV Team
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Upload", description = "Endpoints for uploading files to the PayMeTV platform")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * Redirects a browser GET on /api/files/upload to the React upload page (/upload).
     * This makes the URL directly accessible from the browser address bar.
     */
    @GetMapping("/upload")
    @Operation(
            summary = "Open the file-upload UI",
            description = "Redirects browser navigation to the React upload page at /upload."
    )
    @ApiResponse(responseCode = "302", description = "Redirect to /upload")
    public ResponseEntity<Void> redirectToUploadUi() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/upload"))
                .build();
    }

    /**
     * Accepts a multipart file upload, validates it, and persists it to disk.
     *
     * @param file the multipart file from the HTTP request
     * @return 200 with file metadata on success, 400 on validation error, 500 on I/O error
     */

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload a file",
            description = "Accepts a multipart file upload. Validates size (max 10 MB) and MIME type, "
                    + "then saves the file to the server. Returns metadata about the stored file."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed – file is empty, too large, or has a disallowed MIME type",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error while saving the file",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Map<String, Object>> uploadFile(
            @Parameter(
                    description = "File to upload. Allowed types: image/jpeg, image/png, image/gif, "
                            + "image/webp, video/mp4, video/mpeg, application/pdf. Max size: 10 MB.",
                    required = true
            )
            @RequestParam("file") MultipartFile file
    ) {
        try {
            Map<String, Object> metadata = fileUploadService.saveFile(file);

            Map<String, Object> response = new HashMap<>(metadata);
            response.put("status", "uploaded");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("File upload validation failed: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (IOException e) {
            log.error("Failed to save uploaded file", e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to save file: " + e.getMessage());
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
