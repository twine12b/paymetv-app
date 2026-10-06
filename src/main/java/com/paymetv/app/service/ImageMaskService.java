package com.paymetv.app.service;

import jdk.jfr.Description;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

@Service
@Slf4j
public class ImageMaskService {

    Logger logger = LoggerFactory.getLogger(ImageMaskService.class);

    @Value("${file.upload-dir}")
    private Path prefix;

    @Value("${ml_dir}")
    private String output_prefix;

    @Value("${ml_dummy_source_dir}")
    private File sourceDir;

    @Value("${ml_dummy_destination_dir}")
    private File destDir;

    @Value("${ml_sub_dir}")
    private String ml_sub_dir;

    @Value("${ml_dataset_count}")
    private String dataset_count;

    // reads the raw image file and apply the mask to it, then save the masked image to the same directory
    String cmd = setPythonCmd();

    public String sayHi () { return "Hello from ImageMaskService!";}

    public String removeBackground(String loc, String filename) throws IOException, InterruptedException {

        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "background_removal_tool.py").toAbsolutePath();

        Path inputDirectory = Path.of(prefix.toString(), loc).toAbsolutePath();
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

    public String imageMask(String loc, String filename) throws IOException, InterruptedException {
        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "image_masking_tool.py").toAbsolutePath();

        Path inputDirectory = Path.of(prefix.toString(), loc, "output").toAbsolutePath();
        Path inputPath = inputDirectory.resolve(filename);
        Path outputPath = inputDirectory.resolve(inputDirectory + "/masks").resolve(filename);

        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;

        Files.createDirectories(inputDirectory);
        Path seedImagePath = Path.of(prefix.toString(), loc, "output", filename).toAbsolutePath();
        Files.copy(seedImagePath, inputPath, StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(outputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath.toString(),
                "--location",
                inputDirectory.toString(),
                "--file",
                filename
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

    public String changeFileAspectRatio(String loc, String filename) throws IOException, InterruptedException {
        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "image_resizer.py").toAbsolutePath();

        Path inputDirectory = Path.of(prefix.toString(), loc, "output").toAbsolutePath();
        String inputFileName = filename;
        Path inputPath = inputDirectory.resolve(inputFileName);
        Path outputPath = inputDirectory.resolve(inputDirectory).resolve(filename);

        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath.toString(),
                "--location",
                loc,
                "--file",
                filename
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


        logger.info("image resizer:\n" + output);
        return "success: " + "[" + exitCode + "]";
    }

    @Description("Cleans up test files")
    private void cleanup(Path expectedPath) throws IOException {
        Path userDirectory = Path.of(prefix.toString(), expectedPath.getParent().toString());

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

    public String createDataset(String loc, String out_loc, String username, File file, String group, String sub1) throws IOException, InterruptedException {

        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "cocosynth", "image_composition.py").toAbsolutePath();

        Path inputDirectory = Path.of(loc).toAbsolutePath();
        Path outputDirectory = Path.of(output_prefix, out_loc, "output").toAbsolutePath();

        Path execDir = setupMlDirectory(inputDirectory, username, file.getName(), group, sub1);
        Files.createDirectories(outputDirectory);

        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath.toString(),
                "--input_dir",
                execDir.resolve("input").toString(),
                "--output_dir",
                outputDirectory.toString(),
                "--count",
                dataset_count,
                "--width",
                "1024",
                "--height",
                "1024",
                "--silent"
        );
        processBuilder.redirectErrorStream(true);

        logger.info("Running image composition: {}", processBuilder.command());

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Image composition failed with exit code " + exitCode + ":\n" + output);
        }

        return createCocoAnnotations(outputDirectory, pythonCommand, output);

    }

    private Path setupMlDirectory(Path inputDirectory, String username,
                                  String file, String group, String sub1) throws IOException {
        Path sourceFile = Path.of(inputDirectory.toString(), file);
        Path sourceDir = Path.of(this.sourceDir.toString());
        Path destDir = Paths.get(this.destDir.toString(), username);

        Path subdirectory = destDir.resolve(ml_sub_dir).resolve(group).resolve(sub1);

        // copy dummy file structure using properties file location
        FileUtils.copyDirectory(sourceDir.toFile(), destDir.toFile());

        Files.createDirectories(subdirectory);

        Files.copy(sourceFile, subdirectory.resolve(
                        sourceFile.getFileName()),
                StandardCopyOption.REPLACE_EXISTING);

        return destDir;
    }

    private boolean createCocosynthInfo(String username) {
        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "cocosynth", "coco_json_utils.py").toAbsolutePath();

        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;
        String md_dir = Paths.get(output_prefix, username, "output", "mask_definitions.json").toString();
        String di_dir = Paths.get(output_prefix, username, "output", "dataset_info.json").toString();

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                scriptPath.toString(),
                "-md",
                md_dir,
                "-di",
                di_dir
        );

        // test if file is empty, if so, return false
        File mdFile = new File(md_dir);
        File diFile = new File(di_dir);

        if (mdFile.length() == 0 || diFile.length() == 0) {
            logger.error("Mask definitions or dataset info file is empty");
            return false;
        }
        return true;
    }

    private String createCocoAnnotations(Path outputDirectory, String pythonCommand, StringBuilder output) throws IOException, InterruptedException {
        Path maskDefinitionsPath = outputDirectory.resolve("mask_definitions.json");
        Path datasetInfoPath = outputDirectory.resolve("dataset_info.json");
        if (!Files.isRegularFile(maskDefinitionsPath) || Files.size(maskDefinitionsPath) == 0
                || !Files.isRegularFile(datasetInfoPath) || Files.size(datasetInfoPath) == 0) {
            throw new IOException("CoC-Synth did not create non-empty mask_definitions.json and dataset_info.json files");
        }

        Path annotationScriptPath = Path.of("src", "main", "resources", "ml", "code",
                "cocosynth", "coco_json_utils.py").toAbsolutePath();
        Path annotationsPath = outputDirectory.resolve("coco_instances.json");
        ProcessBuilder annotationProcessBuilder = new ProcessBuilder(
                pythonCommand,
                annotationScriptPath.toString(),
                "--mask_definition", maskDefinitionsPath.toString(),
                "--dataset_info", datasetInfoPath.toString()
        );
        annotationProcessBuilder.redirectErrorStream(true);
        logger.info("Converting CoC-Synth masks to COCO annotations: {}", annotationProcessBuilder.command());

        Process annotationProcess = annotationProcessBuilder.start();
        StringBuilder annotationOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(annotationProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                annotationOutput.append(line).append(System.lineSeparator());
            }
        }
        int annotationExitCode = annotationProcess.waitFor();
        if (annotationExitCode != 0 || !Files.isRegularFile(annotationsPath) || Files.size(annotationsPath) == 0) {
            throw new IOException("COCO annotation conversion failed with exit code " + annotationExitCode
                    + ":\n" + annotationOutput);
        }

        return "success - setup of ml and created COCO annotations at " + annotationsPath
                + System.lineSeparator() + output + annotationOutput;
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
