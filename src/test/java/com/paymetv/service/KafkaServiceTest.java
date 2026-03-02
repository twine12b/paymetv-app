package com.paymetv.service;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.PmtvKafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PmtvKafkaService.
 *
 * Uses embedded Kafka to test producer and consumer functionality
 * without requiring an external Kafka broker.
 *
 * Tests verify:
 * - Producer sends messages successfully
 * - Consumer receives messages
 * - Message format validation (counter and timestamp)
 */
@SpringBootTest(classes = AppApplication.class)
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"pmtv-test-topic"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
public class KafkaServiceTest {

    @Autowired
    private PmtvKafkaService pmtvKafkaService;

    private final List<String> receivedMessages = new ArrayList<>();
    private CountDownLatch latch;

    /**
     * Test listener that captures messages for validation.
     */
    @KafkaListener(topics = "pmtv-test-topic", groupId = "test-group")
    public void testListener(ConsumerRecord<String, String> record) {
        receivedMessages.add(record.value());
        if (latch != null) {
            latch.countDown();
        }
    }

    /**
     * Test that the service is properly configured.
     */
    @Test
    public void testServiceConfiguration() {
        assertNotNull(pmtvKafkaService, "PmtvKafkaService should be autowired");
        assertEquals("pmtv-test-topic", pmtvKafkaService.getTopic());
        assertEquals(500, pmtvKafkaService.getTotalMessages());
    }

    /**
     * Test that producer sends messages successfully.
     * Validates that all 500 messages are sent without errors.
     */
    @Test
    @Timeout(60)
    public void testProducerSendsMessagesSuccessfully() throws Exception {
        // Arrange
        receivedMessages.clear();
        latch = new CountDownLatch(500);

        // Act
        CompletableFuture<Void> sendFuture = pmtvKafkaService.send500Messages();

        // Wait for all messages to be sent
        sendFuture.get(30, TimeUnit.SECONDS);

        // Wait for consumer to receive messages (with timeout)
        boolean received = latch.await(30, TimeUnit.SECONDS);

        // Assert
        assertTrue(received, "Should receive all 500 messages within timeout");
        assertEquals(500, receivedMessages.size(), "Should receive exactly 500 messages");
    }

    /**
     * Test that consumer receives messages.
     * Validates that the consumer listener is working correctly.
     */
    @Test
    @Timeout(30)
    public void testConsumerReceivesMessages() throws Exception {
        // Arrange
        receivedMessages.clear();
        latch = new CountDownLatch(10);

        // Act - Send 10 test messages
        for (int i = 1; i <= 10; i++) {
            String message = String.format("Test message #%d", i);
            pmtvKafkaService.send500Messages();
        }

        // Wait for messages
        boolean received = latch.await(10, TimeUnit.SECONDS);

        // Assert
        assertTrue(received, "Should receive messages within timeout");
        assertTrue(receivedMessages.size() >= 10, "Should receive at least 10 messages");
    }

    /**
     * Test message format validation.
     * Validates that messages follow the expected format:
     * "Message #<counter> from PaymeTv at <ISO-8601-timestamp>"
     */
    @Test
    @Timeout(60)
    public void testMessageFormatValidation() throws Exception {
        // Arrange
        receivedMessages.clear();
        latch = new CountDownLatch(500);

        // Pattern to match: "Message #123 from PaymeTv at 2026-02-27T12:34:56.789Z"
        Pattern messagePattern = Pattern.compile(
                "^Message #\\d+ from PaymeTv at \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"
        );

        // Act
        CompletableFuture<Void> sendFuture = pmtvKafkaService.send500Messages();
        sendFuture.get(30, TimeUnit.SECONDS);
        latch.await(30, TimeUnit.SECONDS);

        // Assert
        assertTrue(receivedMessages.size() > 0, "Should have received messages");

        // Validate format of all received messages
        for (String message : receivedMessages) {
            assertTrue(messagePattern.matcher(message).matches(),
                    "Message should match expected format: " + message);
        }

        // Validate counter sequence (check first few messages)
        int messagesToCheck = Math.min(10, receivedMessages.size());
        for (int i = 0; i < messagesToCheck; i++) {
            String message = receivedMessages.get(i);
            assertTrue(message.contains("Message #" + (i + 1)),
                    "Message should contain correct counter: " + message);
        }
    }
}
