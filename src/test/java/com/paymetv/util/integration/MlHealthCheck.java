package com.paymetv.util.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MlHealthCheck {

    @Value("${ml.api-url}")
    private String mlApiUrl;

    @Test
    void mlPodShouldBeReachable() throws Exception {
        String baseUrl = mlApiUrl.replaceAll("/+$", "");
        URI healthUri = URI.create(baseUrl + "/healthz");
        HttpRequest request = HttpRequest.newBuilder(healthUri)
                .timeout(Duration.ofSeconds(25))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "ML health endpoint " + healthUri + " returned: " + response.body());
        assertTrue(response.body().contains("\"status\":\"ok\""),
                "Unexpected ML health response from " + healthUri + ": " + response.body());
    }
}
