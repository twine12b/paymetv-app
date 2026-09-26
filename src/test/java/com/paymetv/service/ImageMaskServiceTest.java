package com.paymetv.service;

import com.paymetv.app.service.ImageMaskService;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ImageMaskServiceTest {

    private String userDir;
    private MultipartFile multipartFile;

    @Autowired
    private ImageMaskService imageMaskService;

    @BeforeEach
    void setUp() throws IOException {
        this.userDir = "testUser12345";
        File file = new File("test.jpeg");
        this.multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                Files.readAllBytes(file.toPath())
        );
    }

    @Test
    @Order(1)
    @DisplayName("background removal tool service")
    public void callBackgroundRemovalTool_fromJava() throws Exception {

        Path expectedPath = Paths.get(this.userDir, this.multipartFile.getOriginalFilename());

        String file = "test.jpeg";
        String output = imageMaskService.removeBackground(expectedPath.toString(), file);

        assertFalse(Files.exists(expectedPath));
        assertTrue(output.contains("success"), "Python output:\n" + output);
    }

    @Test
    @Order(2)
    @DisplayName("Image masking")
    public void callImageMaskingTool_fromJava() throws Exception {

//        String loc = "testUser12345/output/";
        Path expectedPath = Paths.get(this.userDir, this.multipartFile.getOriginalFilename());

//        Path outputPath = Path.of("src/update/adminTest/output/masks/");
        String file = "test.png";
        String output = imageMaskService.imageMask(expectedPath.toString(), file);

        cleanup(Path.of(expectedPath.toString()));

        assertFalse(Files.exists(expectedPath));
        assertTrue(output.contains("success"), "Python output:\n" + output);
    }

    @Description("Cleans up test files")
    private void cleanup(Path expectedPath) throws IOException {
        Path userDirectory = Path.of("uploads", expectedPath.getParent().toString());

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
