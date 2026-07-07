package com.paymetv.app.service.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest(classes = com.paymetv.app.AppApplication.class)
public class GenericProducerIntegrationTest {

    @Autowired
    private GenericProducer genericProducer;

    @Autowired
    private GenericConsumer genericConsumer; // consumer under test

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate; // mock to verify sends

    @Test
    public void testGenericConsumerDirectInvocation() throws Exception {
        String inputTopic = "topic-file-upload";
        String message = "hello-from-direct-test";

        // invoke the consumer method directly (simulates receiving a Kafka message)
        genericConsumer.consume(message, inputTopic);

        // verify that the consumer attempted to send a processed result to the results topic
        org.mockito.Mockito.verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("topic-processed-results"),
                org.mockito.ArgumentMatchers.argThat(arg -> arg != null && arg.toString().contains(message)));
    }
}
