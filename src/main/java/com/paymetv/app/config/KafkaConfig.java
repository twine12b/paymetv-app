package com.paymetv.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

//    @Bean
//    public ProducerFactory<Object, Object> producerFactory() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(config);
//    }
//
//    @Bean
//    public KafkaTemplate<Object, Object> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }

    /**
     * Declares the "hello-world" topic so Kafka Admin creates it automatically
     * on startup if it does not already exist (mirrors the JS consumer's
     * { topic: 'hello-world', partition: 0 } subscription).
     */
    @Bean
    public NewTopic helloWorldTopic() {
        return TopicBuilder.name("hello-world")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Declares the "pmtv-test-topic" topic for the PmtvKafkaService.
     * This topic is used by the Java service that replicates pmtv-test.js functionality.
     *
     * Configuration matches the JavaScript implementation:
     * - Topic: pmtv-test-topic
     * - Partitions: 1
     * - Replication factor: 1 (for single-broker development)
     */
    @Bean
    public NewTopic pmtvTestTopic() {
        return TopicBuilder.name("pmtv-test-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

