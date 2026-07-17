package com.paymetv.app.service.kafka;

import com.paymetv.app.service.FileUploadService;
import com.paymetv.app.service.ImageFaceService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@ConfigurationProperties(prefix = "app.kafka")
@Component
public class GenericConsumer {

    private static final Logger log = LoggerFactory.getLogger(GenericConsumer.class);

    private static final String RESULTS_TOPIC = "topic-processed-results";

    private final FileUploadService fileUploadService;
    private final ImageFaceService imageFaceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Getter
    private volatile CountDownLatch latch = new CountDownLatch(1);
    @Getter
    private volatile String lastMessage;
    @Getter
    private volatile String lastTopic;

    public GenericConsumer(FileUploadService fileUploadService,
                           ImageFaceService imageFaceService,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.fileUploadService = fileUploadService;
        this.imageFaceService = imageFaceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
//            topics = "test-topic1",
            topics = "#{'${app.kafka.topics}'.split(',')}",
            groupId = "ml-learning-group",
            concurrency = "3"
    )
    public void consume(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        lastMessage = message;
        lastTopic = topic;

        String result = switch (topic) {
            case "topic-file-upload" -> fileUploadService.sayHi();
            case "topic-image-face" -> imageFaceService.sayHi();
            case "generic-producer-test" -> "Generic producer test selected";
            case "test-topic1" -> "Local kafka test message received " + topic;
            case "test-topic2" -> "Local kafka test message received " + topic;
            case "test-topic3" -> "Local kafka test message received " + topic;
            default -> "Received message from unknown topic: " + topic;
        };

        log.info(result);
//        log.info("enriched payload result: {}" ,result);

        kafkaTemplate.send("topic-processed-results",
                topic + ": " + message + " -> " + result);

        latch.countDown();
    }

    public synchronized void resetLatch() {
        this.lastMessage = null;
        this.lastTopic = null;
        this.latch = new CountDownLatch(1);
    }
}
