package com.paymetv.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Spring Kafka equivalent of src/main/resources/kafka/index.js.
 *
 * JS → Java mapping:
 *   kafka.Producer  → KafkaTemplate (auto-configured via spring.kafka.producer.*)
 *   kafka.Consumer  → @KafkaListener (auto-configured via spring.kafka.consumer.*)
 *   producer.on('ready', ...) → @EventListener(ApplicationReadyEvent.class)
 *   consumer.on('message', ...) → @KafkaListener method body
 *   autoCommit: true → spring.kafka.consumer.enable-auto-commit=true
 */
@Service
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private static final String TOPIC = "hello-world";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Fires once the application context is fully started — equivalent to
     * producer.on('ready', () => producer.send([{ topic, messages }], callback)).
     */
    @EventListener(ApplicationReadyEvent.class)
    public void sendHelloWorld() {
        kafkaTemplate.send(TOPIC, "Hello, world!")
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent");
                    } else {
                        log.error("Failed to send message to topic {}: {}", TOPIC, ex.getMessage());
                    }
                });
    }

    /**
     * Equivalent to consumer.on('message', msg => console.log('Received', msg.value)).
     * Partition 0 is pinned to match the JS consumer's { partition: 0 } subscription.
     * Auto-commit is handled by spring.kafka.consumer.enable-auto-commit=true.
     */
    @KafkaListener(
            topicPartitions = @TopicPartition(topic = TOPIC, partitions = "0"),
            groupId = "paymetv-group"
    )
    public void consume(String message) {
        log.info("Received: {}", message);
    }
}

