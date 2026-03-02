package com.paymetv.app.controller;

import com.paymetv.app.service.PmtvKafkaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for PmtvKafkaService operations.
 * 
 * Provides endpoints to trigger Kafka message sending and view service status.
 * This controller allows testing the Java implementation of pmtv-test.js
 * through Swagger UI or REST API calls.
 * 
 * @author PayMeTV Team
 */
@RestController
@RequestMapping("/api/kafka")
@Tag(name = "Kafka Testing", description = "Endpoints for testing Kafka producer/consumer (pmtv-test.js replication)")
public class PmtvKafkaController {

    @Autowired
    private PmtvKafkaService pmtvKafkaService;

    /**
     * Triggers sending 500 messages to pmtv-test-topic.
     * 
     * This endpoint replicates the functionality of pmtv-test.js by sending
     * 500 sequential messages with counter and timestamp to Kafka broker at
     * 192.168.0.165:9093.
     * 
     * @return Response with status and message count
     */
    @PostMapping("/send")
    @Operation(
            summary = "Send 500 test messages to Kafka",
            description = "Sends 500 messages to pmtv-test-topic with format: 'Message #<counter> from PaymeTv at <timestamp>'"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages are being sent"),
            @ApiResponse(responseCode = "500", description = "Error sending messages")
    })
    public ResponseEntity<Map<String, Object>> sendMessages() {
        try {
            CompletableFuture<Void> future = pmtvKafkaService.send500Messages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "sending");
            response.put("message", "Sending 500 messages to " + pmtvKafkaService.getTopic());
            response.put("totalMessages", pmtvKafkaService.getTotalMessages());
            response.put("broker", pmtvKafkaService.getBootstrapServers());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Gets the current configuration and status of the Kafka service.
     * 
     * @return Service configuration details
     */
    @GetMapping("/status")
    @Operation(
            summary = "Get Kafka service status",
            description = "Returns configuration details for the PmtvKafkaService"
    )
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "PmtvKafkaService");
        status.put("topic", pmtvKafkaService.getTopic());
        status.put("broker", pmtvKafkaService.getBootstrapServers());
        status.put("totalMessages", pmtvKafkaService.getTotalMessages());
        status.put("description", "Java implementation of pmtv-test.js");
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * Health check endpoint for Kafka service.
     * 
     * @return Health status
     */
    @GetMapping("/health")
    @Operation(
            summary = "Kafka service health check",
            description = "Returns health status of the Kafka service"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "PmtvKafkaService");
        
        return ResponseEntity.ok(health);
    }
}

