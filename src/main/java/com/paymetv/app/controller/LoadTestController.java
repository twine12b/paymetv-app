package com.paymetv.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/load")
@Tag(name = "Load Testing", description = "Endpoints for generating CPU load to test HPA")
public class LoadTestController {

    private final AtomicLong requestCounter = new AtomicLong(0);
    private final Random random = new Random();

    @GetMapping("/cpu")
    @Operation(
        summary = "Generate CPU load",
        description = "Generates CPU load for a specified duration to test horizontal pod autoscaling"
    )
    @ApiResponse(responseCode = "200", description = "CPU load generation completed")
    public ResponseEntity<Map<String, Object>> generateCpuLoad(
            @Parameter(description = "Duration in milliseconds (default: 1000ms, max: 10000ms)")
            @RequestParam(defaultValue = "1000") int durationMs,
            @Parameter(description = "Intensity level 1-10 (default: 5)")
            @RequestParam(defaultValue = "5") int intensity
    ) {
        long requestId = requestCounter.incrementAndGet();
        long startTime = System.currentTimeMillis();
        
        // Validate parameters
        durationMs = Math.min(durationMs, 10000); // Max 10 seconds
        intensity = Math.max(1, Math.min(10, intensity)); // Clamp between 1-10
        
        // Generate CPU load
        long endTime = startTime + durationMs;
        double result = 0;
        int iterations = 0;
        
        while (System.currentTimeMillis() < endTime) {
            // CPU-intensive operations
            for (int i = 0; i < intensity * 10000; i++) {
                result += Math.sqrt(random.nextDouble()) * Math.sin(random.nextDouble());
                result += Math.pow(random.nextDouble(), 2);
                iterations++;
            }
        }
        
        long actualDuration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("durationMs", actualDuration);
        response.put("intensity", intensity);
        response.put("iterations", iterations);
        response.put("result", result);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fibonacci")
    @Operation(
        summary = "Calculate Fibonacci number",
        description = "Calculates Fibonacci number recursively to generate CPU load"
    )
    @ApiResponse(responseCode = "200", description = "Fibonacci calculation completed")
    public ResponseEntity<Map<String, Object>> fibonacci(
            @Parameter(description = "Fibonacci number to calculate (default: 35, max: 45)")
            @RequestParam(defaultValue = "35") int n
    ) {
        long requestId = requestCounter.incrementAndGet();
        long startTime = System.currentTimeMillis();
        
        // Validate parameter
        n = Math.max(1, Math.min(45, n)); // Clamp between 1-45
        
        long result = calculateFibonacci(n);
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("n", n);
        response.put("result", result);
        response.put("durationMs", duration);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prime")
    @Operation(
        summary = "Find prime numbers",
        description = "Finds all prime numbers up to a limit to generate CPU load"
    )
    @ApiResponse(responseCode = "200", description = "Prime number calculation completed")
    public ResponseEntity<Map<String, Object>> findPrimes(
            @Parameter(description = "Upper limit for prime search (default: 100000, max: 1000000)")
            @RequestParam(defaultValue = "100000") int limit
    ) {
        long requestId = requestCounter.incrementAndGet();
        long startTime = System.currentTimeMillis();
        
        // Validate parameter
        limit = Math.max(100, Math.min(1000000, limit));
        
        int primeCount = countPrimes(limit);
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("limit", limit);
        response.put("primeCount", primeCount);
        response.put("durationMs", duration);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(
        summary = "Get load test statistics",
        description = "Returns statistics about load test requests"
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", requestCounter.get());
        stats.put("timestamp", System.currentTimeMillis());
        stats.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        stats.put("freeMemory", Runtime.getRuntime().freeMemory());
        stats.put("totalMemory", Runtime.getRuntime().totalMemory());
        stats.put("maxMemory", Runtime.getRuntime().maxMemory());
        
        return ResponseEntity.ok(stats);
    }

    // Helper method for recursive Fibonacci calculation (CPU intensive)
    private long calculateFibonacci(int n) {
        if (n <= 1) return n;
        return calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
    }

    // Helper method to count prime numbers
    private int countPrimes(int limit) {
        int count = 0;
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) count++;
        }
        return count;
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}

