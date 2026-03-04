package com.paymetv.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//@Service
@Configuration
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
