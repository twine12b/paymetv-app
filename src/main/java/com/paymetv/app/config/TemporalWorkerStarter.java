package com.paymetv.app.config;

import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "temporal.worker.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(WorkerFactory.class)
public class TemporalWorkerStarter {

    private static final Logger logger = LoggerFactory.getLogger(TemporalWorkerStarter.class);

    private final WorkerFactory workerFactory;
    private final long retryIntervalMillis;
    private final ExecutorService starterExecutor;

    public TemporalWorkerStarter(
            WorkerFactory workerFactory,
            @Value("${temporal.worker.start-retry-interval-ms:5000}") long retryIntervalMillis
    ) {
        this.workerFactory = workerFactory;
        this.retryIntervalMillis = retryIntervalMillis;
        this.starterExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "temporal-worker-starter");
            thread.setDaemon(true);
            return thread;
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorkerWhenApplicationIsReady() {
        starterExecutor.submit(() -> {
            while (!workerFactory.isStarted()) {
                try {
                    workerFactory.start();
                    logger.info("Temporal worker started successfully");
                } catch (Exception ex) {
                    logger.warn("Temporal worker start failed; retrying in {} ms. Cause: {}", retryIntervalMillis, ex.getMessage());
                    try {
                        Thread.sleep(retryIntervalMillis);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
    }

    @PreDestroy
    public void shutdownStarterExecutor() {
        starterExecutor.shutdownNow();
        try {
            if (!starterExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                logger.warn("Temporal worker starter executor did not terminate cleanly");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}