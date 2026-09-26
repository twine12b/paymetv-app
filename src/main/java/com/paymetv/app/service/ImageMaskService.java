package com.paymetv.app.service;

import jdk.jfr.Description;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

@Service
@Slf4j
public class ImageMaskService {

    Logger logger = LoggerFactory.getLogger(ImageMaskService.class);

    @Value("${file.upload-dir}")
    private Path filePath;

    // TODO - read the raw image file and apply the mask to it, then save the masked image to the same directory
    String cmd = setPythonCmd();

    public String sayHi () { return "Hello from ImageMaskService!";}

    public String removeBackground(String loc, String filename) throws IOException, InterruptedException {

        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "background_removal_tool.py").toAbsolutePath();

        Path inputDirectory = Path.of("uploads", loc).toAbsolutePath();
        String inputFileName = "test.jpeg";
        Path inputPath = inputDirectory.resolve(inputFileName);
        Path outputPath = inputDirectory.resolve("output").resolve("test.png");
        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;

        Files.createDirectories(inputDirectory);
        Path seedImagePath = Path.of("test.jpeg").toAbsolutePath();
        Files.copy(seedImagePath, inputPath, StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(outputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath.toString(),
                "--location",
                inputDirectory.toString(),
                "--file",
                inputFileName
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();

        return "removing background successful";
    }

    public String imageMask(String loc, String file) throws IOException, InterruptedException {
        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "image_masking_tool.py").toAbsolutePath();

        Path inputDirectory = Path.of("uploads", loc, "output").toAbsolutePath();
        String inputFileName = "test.png";
        Path inputPath = inputDirectory.resolve(inputFileName);
        Path outputPath = inputDirectory.resolve(inputDirectory + "/masks").resolve("test.png");

        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;

        Files.createDirectories(inputDirectory);
        Path seedImagePath = Path.of("uploads", loc, "output", "test.png").toAbsolutePath();
        System.out.println(seedImagePath.toString());
        Files.copy(seedImagePath, inputPath, StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(outputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath.toString(),
                "--location",
                inputDirectory.toString(),
                "--file",
                inputFileName
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();

        return "masking image successful";
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

    private String setPythonCmd(){
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            logger.info("Windows OS detected");
            return "python";
        } else if (os.contains("mac")) {
            logger.info("macOS OS detected");
            return "python3";
        } else if (os.contains("nux")) {
            logger.info("Linux OS detected");
            return "python3";
        } else {
            logger.error("Unknown OS");
        }
        return null;
    }
}
