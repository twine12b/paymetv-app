package com.paymetv.service;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.FileUploadService;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

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
        String savedFile = fileUploadService.saveFile("test-content".getBytes(), "test.jpeg", "/testUser12345");

        Path savedPath = Paths.get(savedFile).toAbsolutePath().normalize();
        Path expectedBase = Paths.get(uploadDir.strip()).toAbsolutePath().normalize();
        Path expectedPath = expectedBase.resolve("testUser12345").resolve("test.jpeg").normalize();

        try {
            assertTrue(Files.exists(savedPath));
            assertEquals(expectedPath, savedPath);
        } finally {
            cleanup(expectedPath);
        }
    }

    @Description("Cleans up test files")
    private void cleanup(Path expectedPath) throws IOException {
        Path userDirectory = expectedPath.getParent();
        if (Files.exists(userDirectory)) {
            try (var paths = Files.walk(userDirectory)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }
    }
}


