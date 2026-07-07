package com.paymetv.app.service.kafka;

import org.springframework.stereotype.Component;

@Component
public class C2 {

//    private static final Logger log = LoggerFactory.getLogger(C2.class);
//    private static final String RESULTS_TOPIC = "topic-processed-results";
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public C2(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @KafkaListener(
//        topics = {"topic-file-upload", "topic-image-face"},
//        groupId = "ml-learning-group",
//        concurrency = "2"
//    )
//    public void consume(@Payload String message,
//                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//        try {
//            log.info("C2 received message from topic='{}': {}", topic, message);
//
//            // Basic processing placeholder - adapt to your business logic
//            String result = "C2 processed -> " + message;
//
//            // Forward to results topic for downstream consumers
//            kafkaTemplate.send(RESULTS_TOPIC, topic + ": " + result);
//        } catch (Exception e) {
//            log.error("C2 failed to process message from topic='{}': {}", topic, e.getMessage(), e);
//            // Decide whether to rethrow to trigger retry, or swallow after logging
//        }
//    }
}
