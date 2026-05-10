package com.paymetv.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for producing messages to Kafka topics.
 *
 * @author Paymetv Team
 * @version 1.0
 * @since 2024-06-01
 */
@Service
public class ProducerService {

  @Autowired
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public ProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(String topic, Object message) {
    kafkaTemplate.send("ml_streaming", message);
  }
}
