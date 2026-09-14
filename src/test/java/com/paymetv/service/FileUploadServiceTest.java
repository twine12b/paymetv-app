package com.paymetv.service;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AppApplication.class, properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "file.upload-dir=uploads"
})
class FileUploadServiceTest {

    @Autowired
    private FileUploadService fileUploadService;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Test
    void upload_leadingSlashUserDir_isSavedUnderConfiguredUploadDirectory() throws IOException {
        String savedFile = fileUploadService.saveFile("test-content".getBytes(), "test.jpeg", "/adminTest");

        Path savedPath = Paths.get(savedFile).toAbsolutePath().normalize();
        Path expectedBase = Paths.get(uploadDir.strip()).toAbsolutePath().normalize();
        Path expectedPath = expectedBase.resolve("adminTest").resolve("test.jpeg").normalize();

        try {
            assertTrue(Files.exists(savedPath));
            assertEquals(expectedPath, savedPath);
        } finally {
            // Clean up test files
            Files.deleteIfExists(savedPath);
            Files.deleteIfExists(savedPath.getParent());
        }
    }
}


