package com.paymetv.app;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
@Tag(name = "PayMeTV API", description = "REST API for PayMeTV application with item management and metrics")
public class AppApplication implements WebMvcConfigurer {

    private final Map<Integer, String> items = new HashMap<>();
    private MeterRegistry meterRegistry;

    @GetMapping("/api/health")
    @Operation(summary = "Health check endpoint", description = "Returns OK status for the landing page")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    public MeterRegistry meterRegistry() {
        this.meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        return this.meterRegistry;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (meterRegistry != null) {
            registry.addInterceptor(new MetricsInterceptor(meterRegistry));
        }
    }

//    @Operation(summary = "Landing page")
//    @ApiResponse(responseCode = "200", description = "Returns index.html page")
//    @GetMapping(value = "/", produces = "text/html")
//    public String root() {
//        return "forward:/index.html";
//    }

    @PostMapping("/items")
    @Operation(summary = "Create a new item", description = "Creates a new item with the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item created successfully",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> createItem(
            @Parameter(description = "Item to create", required = true)
            @RequestBody Item item) {
        int itemId = items.size() + 1;
        items.put(itemId, item.getName());
        return ResponseEntity.ok(Map.of(
                "item_id", itemId,
                "name", item.getName(),
                "status", "created"
        ));
    }

    @GetMapping("/items")
    @Operation(summary = "Get all items", description = "Retrieves all items in the system")
    @ApiResponse(responseCode = "200", description = "Items retrieved successfully",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<Map<String, Object>> getAllItems() {
        return ResponseEntity.ok(Map.of("items", items));
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Get item by ID", description = "Retrieves an item by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item found and returned successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> readItem(
            @Parameter(description = "ID of the item to retrieve", required = true)
            @PathVariable int itemId) {
        if (!items.containsKey(itemId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("detail", "Item not found"));
        }
        return ResponseEntity.ok(Map.of(
                "item_id", itemId,
                "name", items.get(itemId)
        ));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update an item", description = "Updates an existing item with new data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> updateItem(
            @Parameter(description = "ID of the item to update", required = true)
            @PathVariable int itemId,
            @Parameter(description = "Updated item data", required = true)
            @RequestBody Item item) {
        if (!items.containsKey(itemId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("detail", "Item not found"));
        }
        items.put(itemId, item.getName());
        return ResponseEntity.ok(Map.of(
                "item_id", itemId,
                "name", item.getName(),
                "status", "updated"
        ));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete an item", description = "Deletes an item by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item deleted successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, Object>> deleteItem(
            @Parameter(description = "ID of the item to delete", required = true)
            @PathVariable int itemId) {
        if (!items.containsKey(itemId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("detail", "Item not found"));
        }
        items.remove(itemId);
        return ResponseEntity.ok(Map.of(
                "item_id", itemId,
                "status", "deleted"
        ));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Prometheus metrics", description = "Returns Prometheus-formatted metrics for monitoring")
    @ApiResponse(responseCode = "200", description = "Metrics returned successfully",
            content = @Content(mediaType = "text/plain"))
    public ResponseEntity<String> metrics() {
        if (meterRegistry instanceof PrometheusMeterRegistry) {
            return ResponseEntity.ok().body(((PrometheusMeterRegistry) meterRegistry).scrape());
        }
        return ResponseEntity.ok().body("Metrics not available");
    }

    static class Item {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class MetricsInterceptor implements HandlerInterceptor {
        private final MeterRegistry meterRegistry;
        private final AtomicInteger inProgressRequests;

        public MetricsInterceptor(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
            this.inProgressRequests = new AtomicInteger(0);
            meterRegistry.gauge("http_requests_in_progress", Tags.empty(), inProgressRequests);
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            inProgressRequests.incrementAndGet();
            request.setAttribute("startTime", System.nanoTime());
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            inProgressRequests.decrementAndGet();
            long startTime = (long) request.getAttribute("startTime");
            long duration = System.nanoTime() - startTime;

            String method = request.getMethod();
            String path = request.getRequestURI();
            String status = String.valueOf(response.getStatus());

            // Use the variables to record more detailed metrics
            Tags tags = Tags.of(
                    "method", method,
                    "path", path,
                    "status", status
            );

            meterRegistry.counter("http_request_total", tags).increment();
            meterRegistry.timer("http_request_duration_seconds", tags).record(duration, TimeUnit.NANOSECONDS);
        }
    }
}
