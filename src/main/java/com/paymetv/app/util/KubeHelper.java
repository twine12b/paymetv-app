package com.paymetv.app.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KubeHelper {

    @Value("${ml_namespace:ml}")
    private String namespace;

    @Value("${ml_deployment_name:ml-trainer}")
    private String deploymentName;

    @Value("${ml_script_dir}")
    private String script_dir;

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

    public String getPodName(String namespace, String labelSelector) throws IOException, InterruptedException {
        String output = runKubectl(List.of("kubectl", "-n", namespace, "get", "pods",
                "-l", labelSelector, "--field-selector=status.phase=Running",
                "-o", "jsonpath={.items[0].metadata.name}"));
        if (output.isBlank()) {
            throw new IOException("No running pod found in namespace '" + namespace
                    + "' with label selector '" + labelSelector + "'");
        }
        return output;
    }

    public List<String> defaultCommandList(String active_user, String py_script) {

        return new ArrayList<>(List.of(
                "kubectl", "--request-timeout=0", "exec", "-n", namespace,
                "deployment/" + deploymentName,
                "--", "python3", "-u", script_dir + py_script
        ));
    }
}
