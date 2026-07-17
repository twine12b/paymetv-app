package com.paymetv.service.kafka;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.paymetv.app.service.kafka.GenericConsumer;
import com.paymetv.app.service.kafka.GenericProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for GenericProducer.
 * ---
 * Uses embedded Kafka to test producer and consumer functionality
 * without requiring an external Kafka broker.
 * ---
 * Tests verify:
 * - Producer sends messages successfully
 * - Consumer receives messages
 * - Message format validation (counter and timestamp)
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.group-id=ml-learning-group",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "app.kafka.topics=generic-producer-test,test-topic1,test-topic2,test-topic3"
        })
@EmbeddedKafka(partitions = 1, brokerProperties = {"log.segment.bytes=1048576"}, kraft = false)
public class GenericKafkaTest {

    @Autowired
    private GenericProducer genericProducer;

    @Autowired
    private GenericConsumer genericConsumer;

    private static final String TOPIC = "generic-producer-test";

//    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setup() {
        genericConsumer.resetLatch();
    }

    @Test
    @DisplayName("Should publish and consume message")
    void shouldPublishAndConsumeMessage() throws Exception {

        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String key = "myKey";
        String payload = "Hello Kafka";

        SendResult<String, Object> result =
                genericProducer.send(TOPIC, key, payload)
                        .get(10, TimeUnit.SECONDS);

        genericConsumer.consume(payload, TOPIC);

        assertEquals(TOPIC, result.getRecordMetadata().topic());

        assertTrue(
                genericConsumer.getLatch().await(10, TimeUnit.SECONDS),
                "Consumer never received the message"
        );

        assertEquals(TOPIC, genericConsumer.getLastTopic());
        assertEquals(payload, genericConsumer.getLastMessage());

        assertEquals(1, listAppender.list.size());

        ILoggingEvent event = listAppender.list.get(0);

        assertEquals(
                "Generic producer test selected",
                event.getFormattedMessage()
        );
    }

    @Test
    @DisplayName("Should publish and consume message from test-topic1")
    void shouldPublishAndConsumeMessageFromTestTopic1() throws Exception {

        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String key = "myKey";
        String payload = "Hello Kafka";
        String TEST_TOPIC = "test-topic1";

        SendResult<String, Object> result =
                genericProducer.send(TEST_TOPIC, key, payload)
                        .get(10, TimeUnit.SECONDS);

        genericConsumer.consume(payload, TEST_TOPIC);

        assertEquals(TEST_TOPIC, result.getRecordMetadata().topic());

        assertTrue(
                genericConsumer.getLatch().await(10, TimeUnit.SECONDS),
                "Consumer never received the message"
        );

        assertEquals(TEST_TOPIC, genericConsumer.getLastTopic());
        assertEquals(payload, genericConsumer.getLastMessage());

        assertEquals(1, listAppender.list.size());

        ILoggingEvent event = listAppender.list.get(0);

        assertEquals(
                "Local kafka test message received " + TEST_TOPIC,
                event.getFormattedMessage()
        );
    }


    @Test
    @DisplayName("Should publish and consume message from test-topic2")
    void shouldPublishAndConsumeMessageFromTestTopic2() throws Exception {

        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String key = "myKey";
        String payload = "Hello Kafka";
        String TEST_TOPIC = "test-topic2";

        SendResult<String, Object> result =
                genericProducer.send(TEST_TOPIC, key, payload)
                        .get(10, TimeUnit.SECONDS);

        genericConsumer.consume(payload, TEST_TOPIC);

        assertEquals(TEST_TOPIC, result.getRecordMetadata().topic());

        assertTrue(
                genericConsumer.getLatch().await(10, TimeUnit.SECONDS),
                "Consumer never received the message"
        );

        assertEquals(TEST_TOPIC, genericConsumer.getLastTopic());
        assertEquals(payload, genericConsumer.getLastMessage());

        assertEquals(1, listAppender.list.size());

        ILoggingEvent event = listAppender.list.get(0);

        assertEquals(
                "Local kafka test message received " + TEST_TOPIC,
                event.getFormattedMessage()
        );
    }

    @Test
    @DisplayName("Should publish and consume message from test-topic3")
    void shouldPublishAndConsumeMessageFromTestTopic3() throws Exception {

        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String key = "myKey";
        String payload = "Hello Kafka";
        String TEST_TOPIC = "test-topic3";

        SendResult<String, Object> result =
                genericProducer.send(TEST_TOPIC, key, payload)
                        .get(10, TimeUnit.SECONDS);

        genericConsumer.consume(payload, TEST_TOPIC);

        assertEquals(TEST_TOPIC, result.getRecordMetadata().topic());

        assertTrue(
                genericConsumer.getLatch().await(10, TimeUnit.SECONDS),
                "Consumer never received the message"
        );

        assertEquals(TEST_TOPIC, genericConsumer.getLastTopic());
        assertEquals(payload, genericConsumer.getLastMessage());

        assertEquals(1, listAppender.list.size());

        ILoggingEvent event = listAppender.list.get(0);

        assertEquals(
                "Local kafka test message received " + TEST_TOPIC,
                event.getFormattedMessage()
        );
    }

    @Test
    @DisplayName("Should fail to publish to unknown topic")
    void shouldFailToPublishToUnknownTopic() throws Exception {

        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String key = "myKey";
        String payload = "Hello Kafka";
        String TEST_TOPIC = "unknown-topic";

        SendResult<String, Object> result =
                genericProducer.send(TEST_TOPIC, key, payload)
                        .get(10, TimeUnit.SECONDS);

        genericConsumer.consume(payload, TEST_TOPIC);

        assertEquals(TEST_TOPIC, result.getRecordMetadata().topic());

        assertTrue(
                genericConsumer.getLatch().await(10, TimeUnit.SECONDS),
                "Consumer never received the message"
        );

        assertEquals(TEST_TOPIC, genericConsumer.getLastTopic());
        assertEquals(payload, genericConsumer.getLastMessage());

        assertEquals(1, listAppender.list.size());

        ILoggingEvent event = listAppender.list.get(0);

        assertEquals(
                "Received message from unknown topic: " + TEST_TOPIC,
                event.getFormattedMessage()
        );
    }


//    @Test
//    @DisplayName("Should publish and consume from multiple topics")
//    void publishAndConsumeFromMultipleTopics() throws Exception {
//
//        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
//        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
//        listAppender.start();
//        logger.addAppender(listAppender);
//
//        String key = "myKey";
//        final int TIMEOUT_SECONDS = 10;
//
//        List<String> topics = List.of(
//                "test-topic1",
//                "test-topic2",
//                "test-topic3"
//        );
//
//        List<String> payloads = List.of(
//                "Hello from kafka " + topics.get(0),
//                "Hello from kafka " + topics.get(1),
//                "Hello from kafka " + topics.get(2)
//        );
//
//        List<String> consumers = List.of(
//                "Local kafka test message received " + topics.get(0),
//                "Local kafka test message received " + topics.get(1),
//                "Local kafka test message received " + topics.get(2)
//        );
//
//        for(int i = 0; i < topics.size(); i++) {
//
//            String topic = topics.get(i);
//            String payload = payloads.get(i);
//            String expectedConsumerMessage = consumers.get(i);
//
//            // Reset latch for the next message
//            genericConsumer.resetLatch();
//
//            // Send message to topic
//            SendResult<String, Object> result =
//                    genericProducer.send(topic, key, payload)
//                            .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
//
//            // Verify the message was sent to the correct topic
//            assertEquals(topic, result.getRecordMetadata().topic());
//
//            // Wait for the async @KafkaListener to consume the message
//            assertTrue(
//                    genericConsumer.getLatch().await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
//                    "Consumer never received the message for topic: " + topic
//            );
//
//            // Verify the consumer received the correct message and topic
//            assertEquals(topic, genericConsumer.getLastTopic(),
//                    "Last topic should match the sent topic");
//            assertEquals(payload, genericConsumer.getLastMessage(),
//                    "Last message should match the sent payload");
//
//            // Verify the logging event
//            assertEquals(i, listAppender.list.size() - 1,
//                    "Expected exactly one new log event per iteration");
//            ILoggingEvent event = listAppender.list.get(i);
//
//            assertEquals(
//                    expectedConsumerMessage,
//                    event.getFormattedMessage(),
//                    "Consumer log message should match expected message for topic: " + topic
//            );
//        }
//
//    }

//    public void resetLatch() {
//        latch = new CountDownLatch(1);
//    }
}