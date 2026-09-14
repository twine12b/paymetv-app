package com.paymetv.app.controller;

import com.paymetv.app.domain.Users;
import com.paymetv.app.repository.UserRepository;
import com.paymetv.app.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserRepository userRepository;


    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024L * 1024L; // 2 MB

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/tiff",
            "application/pdf"
    );

    @GetMapping("/upload")
    public ResponseEntity<Void> redirectToUploadUi() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/upload")
                .build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleFileUpload(@RequestPart("file") MultipartFile file) {
        Map<String, Object> response = new LinkedHashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Provided file is empty or missing");
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            response.put("status", "error");
            response.put("message", "Content type '" + contentType + "' is not allowed");
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            response.put("status", "error");
            response.put("message", "File size exceeds the maximum allowed limit");
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.getName() != null) ? auth.getName() : "unknown";
            Long userId = (auth != null && auth.getPrincipal() instanceof Users user) ? user.getId() : null;

            String savedPath = fileUploadService.saveFile(file.getBytes(), file.getOriginalFilename(), username);

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
            String storedName = Paths.get(savedPath).getFileName().toString();

            response.put("status", "uploaded");
            response.put("originalName", originalName);
            response.put("storedName", storedName);
            response.put("size", file.getSize());
            response.put("contentType", contentType);
            response.put("path", savedPath);
            response.put("timestamp", Instant.now().toString());

            // Notify downstream systems
            kafkaTemplate.send("file-uploaded", savedPath);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("timestamp", Instant.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(response);
        }
    }
}
