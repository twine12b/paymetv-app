package com.paymetv.service;

import com.paymetv.app.service.ImageMaskService;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageMaskServiceTest {

    private String userDir;
    private MultipartFile multipartFile;
    private final String prefix = "uploads";
    private final String suffix = "output";

    @Value("${ml_dummy_destination_dir}")
    private String dataset_prefix;

    @Value("${file.upload-dir}")
    private String fs_dir;

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
    @Order(1)
    @DisplayName("runs a simple 'hello world!' test ")
    public void helloTest() throws Exception {
        String active_user = "testUser12345"; // TODO - get session user
        String py_script = "test.py";
        String output = imageMaskService.sayHello(active_user, py_script) ;

        assertTrue(output.contains("ML pod hello script executed successfully"), "Python output:\n" + output);
        assertTrue(output.contains("exitCode 0"), "Python output:\n" + output);
    }

    @Test
    @Order(2)
    @DisplayName("background removal tool service")
    public void callBackgroundRemovalTool_fromJava() throws Exception {

        String active_user = "testUser12345";
        String py_script = "background_removal_tool.py";

        ArrayList<String> filenames = new ArrayList<>(List.of("image1.jpeg", "image2.jpeg", "image3.jpeg"));
        Path expectedPath = Path.of(this.userDir);

        String output = imageMaskService.removeBackground(active_user, py_script, filenames.toArray(String[]::new));

        assertTrue(output.contains("exitCode 0"), "Python output:\n" + output);
    }

    @Test
    @Order(3)
    @DisplayName("Change file aspect ratio - height 800px")
    void changeFileAspectRatio() throws Exception {
        String active_user = "testUser12345";
        String py_script = "image_resizer.py";
        ArrayList<String> filenames = new ArrayList<>(List.of("image1.png", "image2.png", "image3.png"));

        String output = imageMaskService.changeFileAspectRatio(active_user, py_script, filenames.toArray(String[]::new));

        assertTrue(output.contains("exitCode 0"), "Python output:\n" + output);
    }

    @Test
    @Order(4)
    @DisplayName("Move file for cocosynth learning")
    void moveFileForLearning() throws IOException, InterruptedException {

        String active_user = "testUser12345";
        String py_script = "mv_file.py";
        String category = "car";
        String sub_category = "000624";

        String output = imageMaskService.moveFilesForLearning(active_user, py_script, category, sub_category);

        assertTrue(output.contains("exitCode 0"), "Python output:\n" + output);
    }

    @Test
    @Order(5)
    @DisplayName("Create dataset")
    void createDataset() throws Exception {
        String active_user = "testUser12345";
        String py_script = "image_composition.py";

        File file = new File("test-resized.png");
        Path expectedPath = Path.of(this.prefix, this.userDir, suffix);
        Path expectedOutputPath = Path.of(this.userDir);

        String output = imageMaskService.createDataset(active_user, py_script);

        assertTrue(output.contains("exitCode 0"), "Python output:\n" + output);
    }

    @Test
    @Order(6)
    @DisplayName("Create Machine Learning Model")
    void createMachineLearningModel() throws Exception {
        String active_user = "testUser12345";
        String py_script = "train.py";

        String output = imageMaskService.createMachineLearningModel(active_user, py_script);

        assertTrue(output.contains("exitCode 0"), "Python output:\n" + output);
    }

    @Description("Cleans up test files")
    private static void cleanup(Path expectedPath) throws IOException {
        Path userDirectory = Path.of("uploads", expectedPath.getParent().toString());

        System.out.println("Cleaning up");

//        if (Files.exists(userDirectory)) {
//            try (var paths = Files.walk(userDirectory)) {
//                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
//                    try {
//                        Files.deleteIfExists(path);
//                    } catch (IOException e) {
//                        throw new UncheckedIOException(e);
//                    }
//                });
//            }
//        }
    }
}
