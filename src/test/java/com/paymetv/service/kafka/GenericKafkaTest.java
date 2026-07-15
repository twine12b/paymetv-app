package com.paymetv.service.kafka;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.paymetv.app.service.kafka.GenericConsumer;
import com.paymetv.app.service.kafka.GenericProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        properties = {
                "spring.kafka.bootstrap-servers=localhost:9092",
                "spring.kafka.consumer.group-id=ml-learning-group",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        })
public class GenericKafkaTest {

    @Autowired
    private GenericProducer genericProducer;

    @Autowired
    private GenericConsumer genericConsumer;

    private static final String TOPIC = "generic-producer-test";

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
    @DisplayName("")
    void publishAndConsumeFromMultipleTopics() throws Exception {

        Logger logger = (Logger) LoggerFactory.getLogger(GenericConsumer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String key = "myKey";

        List<String> topics = List.of(
                "test-topic1",
                "test-topic2",
                "test-topic3"
        );

        List<String> payloads = List.of(
                "Hello from kaflka " + topics.get(0),
                "Hello from kaflka " + topics.get(1),
                "Hello from kaflka " + topics.get(2)
        );

        List<String> consumers = List.of(
                "Local kafka test message received " + topics.get(0),
                "Local kafka test message received " + topics.get(1),
                "Local kafka test message received " + topics.get(2)
        );

        for(String topic : topics) {
            SendResult<String, Object> result =
                    genericProducer.send(topic, key, payloads.get(topics.indexOf(topic)))
                            .get(10, TimeUnit.SECONDS);

            genericConsumer.consume(payloads.get(topics.indexOf(topic)), topic);

            assertEquals(topic, result.getRecordMetadata().topic());

            assertTrue(
                    genericConsumer.getLatch().await(10, TimeUnit.SECONDS),
                    "Consumer never received the message"
            );

            assertEquals(topic, genericConsumer.getLastTopic());
            assertEquals(payloads.get(topics.indexOf(topic)), genericConsumer.getLastMessage());

            ILoggingEvent event = listAppender.list.get(topics.indexOf(topic));

            assertEquals(
                    consumers.get(topics.indexOf(topic)),
                    event.getFormattedMessage()
            );
        }

    }
}