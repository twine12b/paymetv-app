package com.paymetv.app.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public final class PodDirectoryCopier {
    private final KubeHelper kubeHelper;

    public PodDirectoryCopier(KubeHelper kubeHelper) {
        this.kubeHelper = kubeHelper;
    }

    /**
     * Copies files from one pod directory to another using a local staging directory.
     */
    public String copyFiles(
            String sourceNamespace,
            String sourcePod,
            String sourceDirectory,
            String destinationNamespace,
            String destinationPod,
            String destinationDirectory,
            List<String> fileNames,
            Path stagingDirectory) throws IOException, InterruptedException {

        try {
            kubeHelper.runKubectl(List.of(
                    "kubectl", "-n", destinationNamespace, "exec", destinationPod, "--",
                    "mkdir", "-p", destinationDirectory));

            for (String fileName : fileNames) {
                Path localFile = stagingDirectory.resolve(fileName);

                kubeHelper.runKubectl(List.of(
                        "kubectl", "-n", sourceNamespace, "cp",
                        sourcePod + ":" + sourceDirectory + "/" + fileName,
                        localFile.toString()));

                kubeHelper.runKubectl(List.of(
                        "kubectl", "-n", destinationNamespace, "cp",
                        localFile.toString(),
                        destinationPod + ":" + destinationDirectory + "/" + fileName));
            }

            return "Copied files from pod '" + sourcePod + "' in namespace '"
                    + sourceNamespace + "' to pod '" + destinationPod
                    + "' in namespace '" + destinationNamespace + "' at '"
                    + destinationDirectory + "': " + String.join(", ", fileNames);
        } finally {
            FileUtils.deleteDirectory(stagingDirectory.toFile());
        }
    }

    /**
     * Copies files from a local directory to a pod directory.
     */
    public String copyLocalFiles(
            Path sourceDirectory,
            String destinationNamespace,
            String destinationPod,
            String destinationDirectory,
            List<String> fileNames) throws IOException, InterruptedException {

        kubeHelper.runKubectl(List.of(
                "kubectl", "-n", destinationNamespace, "exec", destinationPod, "--",
                "mkdir", "-p", destinationDirectory));

        for (String fileName : fileNames) {
            Path localFile = sourceDirectory.resolve(fileName);

            kubeHelper.runKubectl(List.of(
                    "kubectl", "-n", destinationNamespace, "cp",
                    localFile.toString(),
                    destinationPod + ":" + destinationDirectory + "/" + fileName));
        }

        return "Copied local files to pod '" + destinationPod + "' in namespace '"
                + destinationNamespace + "' at '" + destinationDirectory + "': "
                + String.join(", ", fileNames);
    }
}