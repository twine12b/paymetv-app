package com.paymetv.service;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AppApplication.class, properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "file.upload-dir=uploads/  "
})
class FileUploadServiceTest {

    @Autowired
    private FileUploadService fileUploadService;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Test
    void upload_savesFileToUploadDirectory() throws IOException {

        System.out.println(uploadDir);

        String userDir = "12345";
        File file = new File("test.jpeg");
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                Files.readAllBytes(file.toPath())
        );

        System.out.println(file.exists() ? "test.jpg exists." : "test.jpg does not exist.");

        // Act
        String savedFile = fileUploadService.saveFile(multipartFile, userDir);

//        try {
//            // Assert
//            assertTrue(Files.exists(file));
//            assertEquals("test-content", Files.readString(savedFile));
//        } finally {
//            Files.deleteIfExists(savedFile);
//        }
    }
}


