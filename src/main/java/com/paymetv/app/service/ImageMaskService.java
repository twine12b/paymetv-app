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

    private ProcessBuilder createProcess(List<String> command) {

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    public String sayHello(String active_user, String py_script) throws IOException, InterruptedException {

        List<String> command = defaultCommandList(active_user, py_script);
        ProcessBuilder proc = createProcess(command);

        Process process = proc.start();

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

        return output.toString().trim() + System.lineSeparator()
                + "ML pod hello script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
    }

    /**
     * python3 background_removal_tool.py \
     *   --location /data/testUser12345 \
     *   --output-dir /data/testUser12345/output \
     *   --file image1.jpeg image2.jpeg
     */
    public String removeBackground(String active_user, String py_script, String... filenames) throws IOException, InterruptedException {
        String suffix = "/output";

        if (filenames == null || filenames.length == 0) {
            throw new IllegalArgumentException("At least one input filename is required");
        }

        List<String> command = defaultCommandList(active_user, py_script);
        command.add("--output-dir");
        command.add(fs_dir + active_user + suffix);
        command.add("--location");
        command.add("/data/" + active_user);
        command.add("--file");
        command.addAll(Arrays.asList(filenames));

        ProcessBuilder proc = createProcess(command);
        Process process = proc.start();

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

        return output.toString().trim() + System.lineSeparator() + "ML pod removeBackground script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
    }

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
    public String changeFileAspectRatio(String active_user, String py_script, String... filenames) throws IOException, InterruptedException {
        String suffix = "/result";

        if (filenames == null || filenames.length == 0) {
            throw new IllegalArgumentException("At least one input filename is required");
        }

        List<String> command = defaultCommandList(active_user, py_script);

        command.add("--location");
        command.add(fs_dir + active_user + "/output");
        command.add("--output-dir");
        command.add(fs_dir + active_user + suffix);
        command.add("--file");
        command.addAll(Arrays.asList(filenames));

        ProcessBuilder proc = createProcess(command);
        Process process = proc.start();

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


        return output.toString().trim() + System.lineSeparator()
                + "ML pod changeFileAspectRatio script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
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



    /** Runs image composition in the ML pod using the user's dataset input directory.
     *
     * python3 image_composition.py \
     *   --input_dir /data/testUser12345/datasets/input \
     *   --output_dir /data/testUser12345/datasets/output \
     *   --count 10 \
     *   --width 1024 \
     *   --height 1024 \
     *   --silent
     *
     */
    public String createDataset(String active_user, String py_script) throws IOException, InterruptedException {

        String datasetDirectory = "/data/" + active_user + "/datasets";

        List<String> command = new ArrayList<>(List.of(
                "kubectl", "exec", "-n", namespace,
                "deployment/" + deploymentName,
                "--", "python3", "-u", "/opt/ml/code/cocosynth/" + py_script,
                "--input_dir", datasetDirectory + "/input",
                "--output_dir", datasetDirectory + "/output",
                "--count", dataset_count,
                "--width", "1024",
                "--height", "1024",
                "--silent"
        ));

        ProcessBuilder proc = createProcess(command);
        logger.info("Running image composition in ML pod: {}", proc.command());

        Process process = proc.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info("Image composition: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Image composition failed with exit code " + exitCode + ":\n" + output);
        }
        logger.info("ML pod createDataset script output: {}", output.toString().trim());
        return output.toString().trim() + System.lineSeparator()
                + "ML pod createDataset script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
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

    public String createMachineLearningModel(String active_user, String py_script) throws IOException, InterruptedException  {

        String datasetPath = "/data/" + active_user + "/datasets/output";

        List<String> command = defaultCommandList(active_user, py_script);
        command.add("--dataset");
        command.add(datasetPath);
        command.add("--epochs");
        command.add(String.valueOf(trainingEpochs));
        command.add("--batch-size");
        command.add(String.valueOf(trainingBatchSize));
        command.add("--max-image-size");
        command.add(String.valueOf(trainingMaxImageSize));

        ProcessBuilder proc = createProcess(command);
        proc.redirectErrorStream(true);

        logger.info("Running Machine Learning Model creation: {}", proc.command());

        Process process = proc.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info("Machine learning training: {}", line);
            }
        }
        int exitCode = process.waitFor();

        log.info("Machine Learning Model creation in namespace '{}' on deployment '{}' with exit code {}:\n{}", namespace, deploymentName, exitCode, output);

        if (exitCode != 0) {
            throw new IOException("Machine Learning Model creation failed in namespace '" + namespace
                    + "' on deployment '" + deploymentName + "' with exit code " + exitCode + ":\n" + output);
        }

//        if (exitCode == 0) {
//            // Store the model files in the model directory and teardown learning material
//            storeModelFiles(Path.of(datasetPath), modelName, active_user);
//            storeModelFiles(Path.of(datasetPath), cocoMetadataName, active_user);
//            cleanupLearningMaterial(active_user);
//        }

//        if (exitCode != 0) {
//            throw new IOException("Machine Learning Model creation failed with exit code " + exitCode + ":\n" + output);
//        }

        return "success - created machine learning model at " + datasetPath
                + System.lineSeparator() + "Training output:" + System.lineSeparator() + output
                + System.lineSeparator() + "ML pod createMachineLearningModel script executed successfully in namespace '" + namespace + "'"
                + " on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
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

//        Path saveTo = Path.of(modelPath.toString(), user_dir);
        Path saveTo = Path.of("/data/", user_dir + "/datasets/output");

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

    private List<String> defaultCommandList(String active_user, String py_script) {

        return new ArrayList<>(List.of(
                "kubectl", "--request-timeout=0", "exec", "-n", namespace,
                "deployment/" + deploymentName,
                "--", "python3", "-u", script_dir + py_script
//                "--location", "/data/" + active_user
        ));
    }

    /**
     *
     * @param active_user
     * @param py_script
     * @param category
     * @param sub_category
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String moveFilesForLearning(String active_user, String py_script, String category, String sub_category) throws IOException, InterruptedException {
        List<String> command = defaultCommandList(active_user, py_script);
        command.add("--active_user");
        command.add(active_user);
        command.add("--category");
        command.add(category);
        command.add("--sub_cat");
        command.add(sub_category);

        ProcessBuilder proc = createProcess(command);
        logger.info("Running move files for learning: {}", proc.command());

        Process process = proc.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.info("Image composition: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Move file into learning directory failed with exit code " + exitCode + ":\n" + output);
        }
        logger.info("ML pod Move files into learning directory script output: {}", output.toString().trim());

        return output.toString().trim() + System.lineSeparator()
                + "ML pod Move files into learning directory script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
    }

    public String moveModelAndCleanup(String activeUser, String itemId) throws IOException, InterruptedException {
        String mlPod = getPodName("ml", "app=ml-trainer");
        String appPod = getPodName("default", "app=paymetv-app");
        String sourceDirectory = "/data/" + activeUser + "/datasets/output";
        String destinationPath = "/app/uploads/" + activeUser + "/" + itemId + "/model";
        List<String> modelFiles = List.of(
                "coco_instances.json",
                "cocosynth_model.pth",
                "cocosynth_model_metadata.json",
                "dataset_info.json",
                "mask_definitions.json"
        );
        Path stagingDirectory = Files.createTempDirectory("paymetv-model-copy-");

        try {
            runKubectl(List.of("kubectl", "-n", "default", "exec", appPod, "--",
                    "mkdir", "-p", destinationPath));

            for (String fileName : modelFiles) {
                Path localFile = stagingDirectory.resolve(fileName);
                runKubectl(List.of("kubectl", "-n", "ml", "cp",
                        mlPod + ":" + sourceDirectory + "/" + fileName, localFile.toString()));
                runKubectl(List.of("kubectl", "-n", "default", "cp",
                        localFile.toString(), appPod + ":" + destinationPath + "/" + fileName));
            }
            return "Copied model files from pod '" + mlPod + "' in namespace 'ml' to pod '"
                    + appPod + "' in namespace 'default' at '" + destinationPath + "': "
                    + String.join(", ", modelFiles);
        } finally {
            FileUtils.deleteDirectory(stagingDirectory.toFile());
        }
    }

    private String runKubectl(List<String> command) throws IOException, InterruptedException {
        Process process = createProcess(command).start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed with exit code " + exitCode + ": "
                    + String.join(" ", command) + System.lineSeparator() + output);
        }
        return output.toString().trim();
    }

    /**
     * Gets the name of the pod running the ML deployment in the specified namespace.
     * @param namespace
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String getPodName(String namespace) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                "kubectl", "-n", namespace, "describe", "deployments"
        ).redirectErrorStream(true).start();

        String output;
        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
            output = reader.lines()
                    .filter(line -> line.contains("NewReplicaSet:"))
                    .map(line -> line.replaceFirst(
                            "^.*NewReplicaSet:\\s*([^\\s]+).*$", "$1"))
                    .findFirst()
                    .orElse("");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("kubectl failed with exit code " + exitCode + ": " + output);
        }
        if (output.isEmpty()) {
            throw new IOException("Could not find a NewReplicaSet name in kubectl output");
        }

        logger.info("Found NewReplicaSet name: {}", output);

        return output;
    }

    private String getPodName(String namespace, String labelSelector) throws IOException, InterruptedException {
        String output = runKubectl(List.of("kubectl", "-n", namespace, "get", "pods",
                "-l", labelSelector, "--field-selector=status.phase=Running",
                "-o", "jsonpath={.items[0].metadata.name}"));
        if (output.isBlank()) {
            throw new IOException("No running pod found in namespace '" + namespace
                    + "' with label selector '" + labelSelector + "'");
        }
        return output;
    }
}
