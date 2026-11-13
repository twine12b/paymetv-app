package com.paymetv.app.service;

import com.paymetv.app.util.KubeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Service
public class ImageMaskService {

    @Value("${file.upload-dir}")
    private String fs_dir;

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

    private final String cmd = setPythonCmd();

    private final KubeHelper kubeHelper;

    // Helper injection, prevts NullPointerException when calling defaultCommandList() in tests
    public ImageMaskService(KubeHelper kubeHelper) {
        this.kubeHelper = kubeHelper;
    }

    public String sayHello(String active_user, String py_script) throws IOException, InterruptedException {

        List<String> command = kubeHelper.defaultCommandList(active_user, py_script);
        ProcessBuilder proc = kubeHelper.createProcess(command);

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
        log.info("ML pod hello script output: {}", output.toString().trim());

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

        List<String> command = kubeHelper.defaultCommandList(active_user, py_script);
        command.add("--output-dir");
        command.add(fs_dir + active_user + suffix);
        command.add("--location");
        command.add("/data/" + active_user);
        command.add("--file");
        command.addAll(Arrays.asList(filenames));

        ProcessBuilder proc = kubeHelper.createProcess(command);
        Process process = proc.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                log.info("Background removal: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Python background removal failed with exit code " + exitCode
                    + ":\n" + output);
        }
        log.info("ML pod removeBackground script output: {}", output.toString().trim());

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

        List<String> command = kubeHelper.defaultCommandList(active_user, py_script);

        command.add("--location");
        command.add(fs_dir + active_user + "/output");
        command.add("--output-dir");
        command.add(fs_dir + active_user + suffix);
        command.add("--file");
        command.addAll(Arrays.asList(filenames));

        ProcessBuilder proc = kubeHelper.createProcess(command);
        Process process = proc.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                log.info("Change File Aspect: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Python change file aspect failed with exit code " + exitCode
                    + ":\n" + output);
        }
        log.info("ML pod changeFileAspectRatio script output: {}", output.toString().trim());


        return output.toString().trim() + System.lineSeparator()
                + "ML pod changeFileAspectRatio script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
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

        ProcessBuilder proc = kubeHelper.createProcess(command);
        log.info("Running image composition in ML pod: {}", proc.command());

        Process process = proc.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                log.info("Image composition: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Image composition failed with exit code " + exitCode + ":\n" + output);
        }
        log.info("ML pod createDataset script output: {}", output.toString().trim());
        return output.toString().trim() + System.lineSeparator()
                + "ML pod createDataset script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
    }

    public String createMachineLearningModel(String active_user, String py_script) throws IOException, InterruptedException  {

        String datasetPath = "/data/" + active_user + "/datasets/output";

        List<String> command = kubeHelper.defaultCommandList(active_user, py_script);
        command.add("--dataset");
        command.add(datasetPath);
        command.add("--epochs");
        command.add(String.valueOf(trainingEpochs));
        command.add("--batch-size");
        command.add(String.valueOf(trainingBatchSize));
        command.add("--max-image-size");
        command.add(String.valueOf(trainingMaxImageSize));

        ProcessBuilder proc = kubeHelper.createProcess(command);
        proc.redirectErrorStream(true);

        log.info("Running Machine Learning Model creation: {}", proc.command());

        Process process = proc.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                log.info("Machine learning training: {}", line);
            }
        }
        int exitCode = process.waitFor();

        log.info("Machine Learning Model creation in namespace '{}' on deployment '{}' with exit code {}:\n{}", namespace, deploymentName, exitCode, output);

        if (exitCode != 0) {
            throw new IOException("Machine Learning Model creation failed in namespace '" + namespace
                    + "' on deployment '" + deploymentName + "' with exit code " + exitCode + ":\n" + output);
        }

        return "success - created machine learning model at " + datasetPath
                + System.lineSeparator() + "Training output:" + System.lineSeparator() + output
                + System.lineSeparator() + "ML pod createMachineLearningModel script executed successfully in namespace '" + namespace + "'"
                + " on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
    }

    private String setPythonCmd() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            log.info("Windows OS detected");
            return "python";
        } else if (os.contains("mac")) {
            log.info("macOS OS detected");
            return "python3";
        } else if (os.contains("nux")) {
            log.info("Linux OS detected");
            return "python3";
        } else {
            log.error("Unknown OS");
        }
        return null;
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
        List<String> command = kubeHelper.defaultCommandList(active_user, py_script);
        command.add("--active_user");
        command.add(active_user);
        command.add("--category");
        command.add(category);
        command.add("--sub_cat");
        command.add(sub_category);

        ProcessBuilder proc = kubeHelper.createProcess(command);
        log.info("Running move files for learning: {}", proc.command());

        Process process = proc.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                log.info("Image composition: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Move file into learning directory failed with exit code " + exitCode + ":\n" + output);
        }
        log.info("ML pod Move files into learning directory script output: {}", output.toString().trim());

        return output.toString().trim() + System.lineSeparator()
                + "ML pod Move files into learning directory script executed successfully in namespace '" + namespace
                + "' on deployment '" + deploymentName + "'"
                + " with exitCode " + exitCode;
    }

    public String moveModelAndCleanup(String activeUser, String itemId) throws IOException, InterruptedException {
        String mlPod = kubeHelper.getPodName("ml", "app=ml-trainer");
        String appPod = kubeHelper.getPodName("default", "app=paymetv-app");
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
            kubeHelper.runKubectl(List.of("kubectl", "-n", "default", "exec", appPod, "--",
                    "mkdir", "-p", destinationPath));

            for (String fileName : modelFiles) {
                Path localFile = stagingDirectory.resolve(fileName);
                kubeHelper.runKubectl(List.of("kubectl", "-n", "ml", "cp",
                        mlPod + ":" + sourceDirectory + "/" + fileName, localFile.toString()));
                kubeHelper.runKubectl(List.of("kubectl", "-n", "default", "cp",
                        localFile.toString(), appPod + ":" + destinationPath + "/" + fileName));
            }
            return "Copied model files from pod '" + mlPod + "' in namespace 'ml' to pod '"
                    + appPod + "' in namespace 'default' at '" + destinationPath + "': "
                    + String.join(", ", modelFiles);
        } finally {
            FileUtils.deleteDirectory(stagingDirectory.toFile());
        }
    }
}
