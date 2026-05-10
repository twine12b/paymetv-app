package com.paymetv.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending messages to Kafka topics.
 *
 * @author PayMeTV Team
 * @version 1.0
 * @since 2024-06-01
 */
@Service
public class KafkaArtifactService {

    /** The KafkaTemplate used for sending messages to Kafka topics. */
    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

      /**
      * Sends a message to the specified Kafka topic.
      *
      * @param topic   the name of the Kafka topic
      * @param message the message to be sent
      */
    public void sendMessage(final String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }

    /*  *
     * Constructor for KafkaArtifactService.
     *
     * @param kafkaTemplate the KafkaTemplate to be used for sending messages
     */
    public KafkaArtifactService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
