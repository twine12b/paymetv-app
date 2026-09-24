package com.paymetv.service;

import com.paymetv.app.service.ImageMaskService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ImageMaskServiceTest {

    @Autowired
    private ImageMaskService imageMaskService;

    @Test
    @Order(1)
    @DisplayName("background removal tool service")
    public void callBackgroundRemovalTool_fromJava() throws Exception {

        String loc = "src/test/resources/";
        String file = "test.jpeg";

        String output = imageMaskService.removeBackground(loc, file);

        // TODO - Create assertions
//        assertEquals(0, exitCode, "Python output:\n" + output);
//        assertTrue(output.toString().contains("Success"), "Python output:\n" + output);
    }

    @Test
    @Order(2)
    @DisplayName("Image masking")
    public void callImageMaskingTool_fromJava() throws Exception {

        String loc = "src/update/adminTest/output/";
//        Path outputPath = Path.of("src/update/adminTest/output/masks/");
        String file = "test.png";

        String output = imageMaskService.imageMask(loc, file);

        // TODO - Create assertions
//        assertEquals(0, exitCode, "Python output:\n" + output);
//        assertTrue(output.toString().contains("Successful"), "Python output:\n" + output);
//        assertTrue(Files.exists(outputPath), "Expected output image at: " + outputPath + "\nPython output:\n" + output);
    }
}
