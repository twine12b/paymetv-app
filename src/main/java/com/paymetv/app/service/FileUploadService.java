package com.paymetv.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling file upload business logic.
 *
 * Responsibilities:
 * - Validate file size (max 2 MB)
 * - Validate MIME type against an allowed list
 * - Save the file to the configured upload directory
 * - Return metadata about the saved file
 *
 * @author PayMeTV Team
 */
@Service
public class FileUploadService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    /** Maximum accepted file size: 10 MB */
    public static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024;

    /** MIME types permitted for upload */
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "video/mp4",
            "video/mpeg",
            "application/pdf"
    );

    /** Directory where uploaded files are persisted */
    // TODO - set this to a PV or S3
    private static final String UPLOAD_DIR = "uploads";

    /**
     * Validates and saves the supplied multipart file.
     *
     * @param file the file received from the HTTP request
     * @return a map containing originalName, storedName, size, and contentType
     * @throws IllegalArgumentException if the file fails size or type validation
     * @throws IOException              if the file cannot be written to disk
     */
    public Map<String, Object> saveFile(MultipartFile file) throws IOException {
        validateFile(file);

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
        }

        String originalFilename = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "unknown";
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        Path destination = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        log.info("Saved file '{}' as '{}' ({} bytes)", originalFilename, storedFilename, file.getSize());

        System.out.println("Lets create a Json payLoad and put that on a kafka topic, change the name of deployed kafka to ml");
        kafkaTemplate.send("file-uploaded", "Hello World");

        return Map.of(
                "originalName", originalFilename,
                "storedName",   storedFilename,
                "size",         file.getSize(),
                "contentType",  file.getContentType() != null ? file.getContentType() : "unknown",
                "path",         destination.toAbsolutePath().toString()
        );
    }

    /**
     * Validates file size and MIME type.
     *
     * @param file the file to validate
     * @throws IllegalArgumentException when a constraint is violated
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file was provided or the file is empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    String.format("File size %d bytes exceeds the maximum allowed size of %d bytes.",
                            file.getSize(), MAX_FILE_SIZE_BYTES)
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    String.format("Content type '%s' is not allowed. Permitted types: %s",
                            contentType, ALLOWED_CONTENT_TYPES)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}

