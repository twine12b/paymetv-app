package com.paymetv.app.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class KubeHelper {

    public ProcessBuilder createProcess(List<String> command) {

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    public String runKubectl(List<String> command) throws IOException, InterruptedException {
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

        log.info("Found NewReplicaSet name: {}", output);

        return output;
    }
}
