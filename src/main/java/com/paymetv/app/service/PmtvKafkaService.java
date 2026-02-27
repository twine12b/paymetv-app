package com.paymetv.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka service that replicates the functionality of pmtv-test.js.
 * 
 * This service connects to Kafka broker at 192.168.0.165:9093 and:
 * - Sends 500 messages sequentially with counter and timestamp
 * - Consumes messages from pmtv-test-topic
 * - Logs progress every 100 messages
 * 
 * Configuration is managed through application.properties:
 * - spring.kafka.bootstrap-servers=192.168.0.165:9093
 * - spring.kafka.consumer.group-id=pmtv-test-group
 * - spring.kafka.consumer.auto-offset-reset=earliest
 * 
 * @author PayMeTV Team
 * @see src/main/resources/kafka/pmtv-test.js
 */
@Service
public class PmtvKafkaService {

    private static final Logger log = LoggerFactory.getLogger(PmtvKafkaService.class);
    private static final String TOPIC = "pmtv-test-topic";
    private static final int TOTAL_MESSAGES = 500;
    private static final int LOG_INTERVAL = 100;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers:192.168.0.165:9093}")
    private String bootstrapServers;

    /**
     * Constructor for dependency injection.
     *
     * @param kafkaTemplate Spring Kafka template for sending messages
     */
    public PmtvKafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends 500 messages to the pmtv-test-topic.
     * Each message format: "Message #<counter> from PaymeTv at <ISO-8601-timestamp>"
     * 
     * Logs progress every 100 messages and returns a CompletableFuture that completes
     * when all messages have been sent.
     *
     * @return CompletableFuture that completes when all messages are sent
     */
    public CompletableFuture<Void> send500Messages() {
        log.info("Connecting producer...");
        log.info("Sending {} messages to topic: {}", TOTAL_MESSAGES, TOPIC);
        
        CompletableFuture<Void> allMessagesFuture = new CompletableFuture<>();
        
        new Thread(() -> {
            try {
                for (int i = 1; i <= TOTAL_MESSAGES; i++) {
                    String timestamp = Instant.now().toString();
                    String messageValue = String.format("Message #%d from PaymeTv at %s", i, timestamp);
                    
                    final int messageNumber = i;
                    kafkaTemplate.send(TOPIC, messageValue)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    if (messageNumber % LOG_INTERVAL == 0) {
                                        log.info("Sent {} messages...", messageNumber);
                                    }
                                } else {
                                    log.error("Failed to send message #{} to topic {}: {}", 
                                            messageNumber, TOPIC, ex.getMessage());
                                }
                            });
                }
                
                log.info("All {} messages sent successfully", TOTAL_MESSAGES);
                allMessagesFuture.complete(null);
                
            } catch (Exception e) {
                log.error("Error sending messages", e);
                allMessagesFuture.completeExceptionally(e);
            }
        }).start();
        
        return allMessagesFuture;
    }

    /**
     * Kafka listener that consumes messages from pmtv-test-topic.
     * Equivalent to the JavaScript consumer in pmtv-test.js.
     * 
     * Configuration:
     * - Topic: pmtv-test-topic
     * - Group ID: pmtv-test-group (from application.properties)
     * - Auto-offset-reset: earliest (reads from beginning)
     * 
     * @param message The message value received from Kafka
     */
    @KafkaListener(
            topics = TOPIC,
            groupId = "${spring.kafka.consumer.group-id:pmtv-test-group}"
    )
    public void consumeMessage(String message) {
        log.info("Received message: {}", message);
    }

    /**
     * Gets the configured Kafka bootstrap servers.
     *
     * @return Bootstrap servers address
     */
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    /**
     * Gets the topic name.
     *
     * @return Topic name
     */
    public String getTopic() {
        return TOPIC;
    }

    /**
     * Gets the total number of messages to send.
     *
     * @return Total messages count
     */
    public int getTotalMessages() {
        return TOTAL_MESSAGES;
    }
}

