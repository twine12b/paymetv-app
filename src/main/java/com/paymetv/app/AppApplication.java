package com.paymetv.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@SpringBootApplication
@RestController
public class AppApplication {

    @GetMapping("/api/health")
    @Operation(summary = "Health check endpoint", description = "Returns OK status for the landing page")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}
