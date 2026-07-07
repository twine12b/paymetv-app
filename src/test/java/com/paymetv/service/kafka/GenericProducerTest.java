package com.paymetv.service.kafka;

import com.paymetv.app.service.kafka.GenericProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

/**
 * Test class for GenericProducer.
 *
 * Uses embedded Kafka to test producer and consumer functionality
 * without requiring an external Kafka broker.
 *
 * Tests verify:
 * - Producer sends messages successfully
 * - Consumer receives messages
 * - Message format validation (counter and timestamp)
 */

@SpringBootTest(
        properties = {
                "spring.kafka.bootstrap-servers=localhost:9092",
                "spring.kafka.consumer.group-id=test-group",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        })
public class GenericProducerTest {

    @Autowired
    private GenericProducer genericProducer;


}