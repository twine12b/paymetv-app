package com.paymetv.service;

import com.paymetv.app.service.ImageMaskService;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ImageMaskServiceTest {

    private String userDir;
    private MultipartFile multipartFile;
    private final String prefix = "uploads";
    private final String suffix = "output";

    @Value("${ml_dummy_destination_dir}")
    private String dataset_prefix;

    @Value("${ml_permanent_suffix}")
    private String dataset_suffix;

    @Value("${ml_coco_model_name}")
    private String modelName;

    @Value("${ml_coco_metadata_name}")
    private String cocoMetadataName;

    @Value("${ml_permanent_prefix}")
    private String model_permanent_prefix;

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

    @AfterAll
    static void tearDown() throws IOException {
        String userDir = "testUser12345";
        File file = new File("test.jpeg");

        Path expectedPath = Paths.get(userDir, file.getName());
        cleanup(expectedPath);
    }

    @Test
    @Order(0)
    @DisplayName("background removal tool service")
    public void callBackgroundRemovalTool_fromJava() throws Exception {

        Path expectedPath = Path.of(this.userDir);

        String file = "test.jpeg";
        String output = imageMaskService.removeBackground(expectedPath.toString(), file);

        assertFalse(Files.exists(expectedPath));
        assertTrue(output.contains("success"), "Python output:\n" + output);
    }

    @Test
    @Order(1)
    @DisplayName("Change file aspect ratio - height 800px")
    void changeFileAspectRatio() throws Exception {

        Path userDir = Paths.get(this.userDir);

        Path expectedPath = Path.of(prefix,userDir.toString(), suffix);

        String file = "test.png";
        String output = imageMaskService.changeFileAspectRatio(expectedPath.toString(), file);

        assertTrue(output.contains("success"), "Python output:\n" + output);
    }

    @Test
    @Order(2)
    @DisplayName("Create dataset")
    void createDataset() throws Exception {
        File file = new File("test-resized.png");
        Path expectedPath = Path.of(this.prefix, this.userDir, suffix);
        Path expectedOutputPath = Path.of(this.userDir);
        String sub1 = "000624";
        String group = "cars";

        String output = imageMaskService.createDataset(expectedPath.toString(),
                expectedOutputPath.toString(), this.userDir, file, group, sub1);

        assertTrue(output.contains("success"));
    }

    @Test
    @Order(3)
    @DisplayName("Create Machine Learning Model")
    void createMachineLearningModel() throws Exception {
        String user = "testUser12345";
        String dataset_suffix = "/output";
        Path datasetPath = Path.of(dataset_prefix, user, dataset_suffix);
        String output = imageMaskService.createMachineLearningModel(datasetPath, user);

        assertTrue(output.contains("success"), "Python output:\n" + output);

        Path modelPath = Path.of(model_permanent_prefix, user);

        System.out.println("Model path: " + modelPath.toString());

        // TODO - check files have been created in the model directory
        assertTrue(Files.exists(modelPath.resolve("cocosynth_model.pth")),
                "Model file not found in the expected directory: "
                        + datasetPath.resolve("cocosynth_model.pth").toString());

        assertTrue(Files.exists(modelPath.resolve("cocosynth_model_metadata.json")),
                "Model file not found in the expected directory: "
                        + datasetPath.resolve("cocosynth_model_metadata.json").toString());

    }

    @Description("Cleans up test files")
    private static void cleanup(Path expectedPath) throws IOException {
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
