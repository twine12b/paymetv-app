package com.paymetv.app.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> CompletableFuture<SendResult<String, Object>> send (
            String topic,
            String key,
            T payload) {

                log.info("Sending data to topic {} with payload {}", topic, payload);

        return kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message to topic={}", topic, ex);
                    } else {
                        log.info(
                                "Message sent. topic={} partition={} offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}
