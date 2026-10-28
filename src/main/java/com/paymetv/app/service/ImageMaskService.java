package com.paymetv.app.service;

import jdk.jfr.Description;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Slf4j
public class ImageMaskService {

    Logger logger = LoggerFactory.getLogger(ImageMaskService.class);

    @Value("${file.upload-dir}")
    private String fs_dir;

    @Value("${ml_script_dir}")
    private String script_dir;

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

    @Value("${ml_permanent_prefix}")
    private Path modelPath;

    @Value("${ml_permanent_suffix}")
    private Path modelPathSuffix;

    @Value("${ml_coco_model_name}")
    private String modelName;

    @Value("${ml_coco_metadata_name}")
    private String cocoMetadataName;

    @Value("${training_batch-size}")
    private int trainingBatchSize;

    @Value("${training_epochs}")
    private int trainingEpochs;

    @Value("${training_max-image-size}")
    private int trainingMaxImageSize;

    @Value("${ml_namespace:ml}")
    private String namespace;

    @Value("${ml_deployment_name:ml-trainer}")
    private String deploymentName;

    // reads the raw image file and apply the mask to it, then save the masked image to the same directory
    private final String cmd = setPythonCmd();

    private ProcessBuilder createProcess(String active_user, String py_script, String dir_suffix, String... args) {

        List<String> command = new ArrayList<>(List.of(
                "kubectl", "exec", "-n", "ml",
                "deployment/" + "ml-trainer",
                "--", "python3", "-u", script_dir + py_script,
                "--location", "/data/" + active_user,
                "--output-dir", fs_dir + active_user + dir_suffix
        ));

        if (args.length > 0) {
            command.add("--file");
            command.addAll(Arrays.asList(args));
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

//    private void copyFileToMlPod(Path source, String remoteDirectory, String filename)
//            throws IOException, InterruptedException {
//        Process process = new ProcessBuilder(
//                "kubectl", "exec", "-i", "-n", namespace,
//                "deployment/" + deploymentName, "--",
//                "sh", "-c", "mkdir -p -- \"$1\" && cat > \"$1/$2\"", "sh",
//                remoteDirectory, filename
//        ).redirectErrorStream(true).start();
//
//        try (OutputStream podInput = process.getOutputStream()) {
//            Files.copy(source, podInput);
//        }
//
//        StringBuilder output = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append(System.lineSeparator());
//            }
//        }
//
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            throw new IOException("Could not copy input image to ML pod (exit code " + exitCode + "):\n" + output);
//        }
//    }

    public String sayHello(String active_user, String py_script) throws IOException, InterruptedException {
        String suffix = "";

        Process process = createProcess(active_user, py_script, suffix).start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Python hello script failed in namespace '" + namespace
                    + "' on deployment '" + deploymentName + "' with exit code " + exitCode + ":\n" + output);
        }
        logger.info("ML pod hello script output: {}", output.toString().trim());

        return output.toString().trim();
    }

    /**
     * python3 background_removal_tool.py \
     *   --location /data/testUser12345 \
     *   --output-dir /data/testUser12345/output \
     *   --file image1.jpeg image2.jpeg
     */
    public Integer removeBackground(String active_user, String py_script, String... filenames) throws IOException, InterruptedException {
        String suffix = "/output";

        if (filenames == null || filenames.length == 0) {
            throw new IllegalArgumentException("At least one input filename is required");
        }

        Process process = createProcess(active_user, py_script, suffix, filenames).start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info("Background removal: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Python background removal failed with exit code " + exitCode
                    + ":\n" + output);
        }
        logger.info("ML pod removeBackground script output: {}", output.toString().trim());

        return exitCode;
    }


//    public String imageMask(String loc, String filename) throws IOException, InterruptedException {
//        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
//        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "image_masking_tool.py").toAbsolutePath();
//
//        Path inputDirectory = Path.of(fs_dir.toString(), loc, "output").toAbsolutePath();
//        Path inputPath = inputDirectory.resolve(filename);
//        Path outputPath = inputDirectory.resolve(inputDirectory + "/masks").resolve(filename);
//
//        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;
//
//        Files.createDirectories(inputDirectory);
//        Path seedImagePath = Path.of(fs_dir.toString(), loc, "output", filename).toAbsolutePath();
//        Files.copy(seedImagePath, inputPath, StandardCopyOption.REPLACE_EXISTING);
//        Files.deleteIfExists(outputPath);
//
//        ProcessBuilder processBuilder = buildPythonProcess(loc, filename);
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();
//        StringBuilder output = new StringBuilder();
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append(System.lineSeparator());
//            }
//        }
//
//        int exitCode = process.waitFor();
//
//        return "masking image successful";
//    }

    /**
     * python3 background_removal_tool.py
     *  --location /data/testUser12345/output
     *  --output /data/testUser12345/result
     *  --file image1.png image2.png image3.png
     *
     * @param active_user
     * @param py_script
     * @param filenames
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Integer changeFileAspectRatio(String active_user, String py_script, String... filenames) throws IOException, InterruptedException {
        String suffix = "/result";

        if (filenames == null || filenames.length == 0) {
            throw new IllegalArgumentException("At least one input filename is required");
        }

        Process process = createProcess(active_user + "/output", py_script, suffix, filenames).start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info("Change File Aspect: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Python change file aspect failed with exit code " + exitCode
                    + ":\n" + output);
        }
        logger.info("ML pod changeFileAspectRatio script output: {}", output.toString().trim());

        return exitCode;

    }

    @Description("Cleans up test files")
    private void cleanup(Path expectedPath) throws IOException {
        Path userDirectory = Path.of(fs_dir.toString(), expectedPath.getParent().toString());

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

    public String createDataset(String loc, String out_loc, String username, File file, String group, String sub1, String... filenames) throws IOException, InterruptedException {
        String suffix = "/result";

        if (filenames == null || filenames.length == 0) {
            throw new IllegalArgumentException("At least one input filename is required");
        }


        return null;
//        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
//        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "cocosynth", "image_composition.py").toAbsolutePath();
//
//        Path inputDirectory = Path.of(loc).toAbsolutePath();
//        Path outputDirectory = Path.of(output_prefix, out_loc, "output").toAbsolutePath();
//
//        Path execDir = setupMlDirectory(inputDirectory, username, file.getName(), group, sub1);
//        Files.createDirectories(outputDirectory);
//
//        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;
//
//        ProcessBuilder processBuilder = new ProcessBuilder(
//                pythonCommand,
//                scriptPath.toString(),
//                "--input_dir",
//                execDir.resolve("input").toString(),
//                "--output_dir",
//                outputDirectory.toString(),
//                "--count",
//                dataset_count,
//                "--width",
//                "1024",
//                "--height",
//                "1024",
//                "--silent"
//        );
//        processBuilder.redirectErrorStream(true);
//
//        logger.info("Running image composition: {}", processBuilder.command());
//
//        Process process = processBuilder.start();
//        StringBuilder output = new StringBuilder();
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append(System.lineSeparator());
//            }
//        }
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            throw new IOException("Image composition failed with exit code " + exitCode + ":\n" + output);
//        }
//
//        return createCocoAnnotations(outputDirectory, pythonCommand, output);

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

    public String createMachineLearningModel(@NonNull Path datasetPath, String userdir) throws IOException, InterruptedException  {
        Path venvPythonPath = Path.of("src", "main", "resources", "ml", ".venv", "bin", cmd).toAbsolutePath();
        Path scriptPath = Path.of("src", "main", "resources", "ml", "code", "train.py").toAbsolutePath();

        String pythonCommand = Files.exists(venvPythonPath) ? venvPythonPath.toString() : cmd;

        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonCommand,
                "-u",
                scriptPath.toString(),
                "--dataset",
                datasetPath.toString(),
                "--epochs",
                String.valueOf(trainingEpochs),
                "--batch-size",
                String.valueOf(trainingBatchSize),
                "--max-image-size",
                String.valueOf(trainingMaxImageSize)
        );

        processBuilder.redirectErrorStream(true);

        logger.info("Running Machine Learning Model creation: {}", processBuilder.command());

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info("Machine learning training: {}", line);
            }
        }
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            // Store the model files in the model directory and teardown learning material
            storeModelFiles(datasetPath, modelName, userdir);
            storeModelFiles(datasetPath, cocoMetadataName, userdir);
            cleanupLearningMaterial(userdir);
        }

        if (exitCode != 0) {
            throw new IOException("Machine Learning Model creation failed with exit code " + exitCode + ":\n" + output);
        }

        return "success - created machine learning model at " + datasetPath;
    }

    private void cleanupLearningMaterial(String userDir) throws IOException {
        if (Files.exists(Path.of(destDir.toString(), userDir))) {
            try (var paths = Files.walk(Path.of(destDir.toString(), userDir))) {
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

    /**
     * Stores the machine learning model files in the permanent local storage directory.
     * @param datasetPath
     * @param file
     * @param user_dir
     * @throws IOException
     */
    public void storeModelFiles(Path datasetPath, String file, String user_dir) throws IOException {
        logger.info("Storing machine learning model at: {}", datasetPath);
        logger.info("Model file to store: {}", file);

        Path saveTo = Path.of(modelPath.toString(), user_dir);

        Files.createDirectories(saveTo);
        Files.copy(datasetPath.resolve(file),
                saveTo.resolve(file),
               StandardCopyOption.REPLACE_EXISTING);

        logger.info("Model file stored: {}", datasetPath.toString() + "/" + file);
    }

    private String setPythonCmd() {
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

    public ProcessBuilder buildPythonProcess(String path, String filename) throws IOException {

        return new ProcessBuilder(
                "kubectl", "exec", "-i", "-n", namespace,
                "deploy/" + deploymentName, "--",
                ".venv/bin/python",
                "code/" + filename
        );

    }

}
