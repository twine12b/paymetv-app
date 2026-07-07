package com.paymetv.app.service.kafka;

import com.paymetv.app.service.FileUploadService;
import com.paymetv.app.service.ImageFaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class GenericConsumer {

    private static final Logger log = LoggerFactory.getLogger(GenericConsumer.class);

    private static final String RESULTS_TOPIC = "topic-processed-results";

    private final FileUploadService fileUploadService;
    private final ImageFaceService imageFaceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String topicName = "ml-learning-group";

    public GenericConsumer(FileUploadService fileUploadService,
                           ImageFaceService imageFaceService,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.fileUploadService = fileUploadService;
        this.imageFaceService = imageFaceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
        topics = {"topic-file-upload", "topic-image-face"},
        groupId = "ml-learning-group",
        concurrency = "3"
    )
    public void consume(@Payload String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        String result = switch (topic) {
//            case "topic-file-upload" -> "file upload switch case executed";
//            case "topic-image-face" -> "image face switch case executed";
            case "topic-file-upload" -> fileUploadService.sayHi();
            case "topic-image-face" -> imageFaceService.sayHi();
            default -> "Received message from unknown topic: " + topic;
        };

        kafkaTemplate.send(RESULTS_TOPIC, topic + ": " + message + " -> " + result);
    }
}
