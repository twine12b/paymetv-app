package com.paymetv.app.service.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = com.paymetv.app.AppApplication.class)
public class GenericProducerImplIntegrationTest {

    @Autowired
    private GenericProducerImpl genericProducerImpl;

    @Autowired
    private GenericConsumerImpl genericConsumerImpl;

    @Autowired
    private TestResultsListener testResultsListener;

    @TestConfiguration
    static class TestKafkaConfig {

        @Bean
        @Primary
        public ProducerFactory<String, Object> producerFactory() {
            Map<String, Object> config = new HashMap<>();
            String brokers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "127.0.0.1:9092");
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        @Primary
        public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }

        @Bean
        public TestResultsListener testResultsListener() {
            return new TestResultsListener();
        }
    }

    @Component
    public static class TestResultsListener {
        private volatile String lastProcessedResult = null;
        private CountDownLatch latch = new CountDownLatch(1);

        @KafkaListener(topics = "topic-processed-results", groupId = "test-results-group")
        public void listen(String message) {
            this.lastProcessedResult = message;
            latch.countDown();
        }

        public String getLastResult() {
            return lastProcessedResult;
        }

        public boolean waitForResult(long timeoutSeconds) throws InterruptedException {
            return latch.await(timeoutSeconds, TimeUnit.SECONDS);
        }

        public void reset() {
            lastProcessedResult = null;
            latch = new CountDownLatch(1);
        }
    }

    @Test
    public void testE2EGenericProducerToGenericConsumer() throws Exception {
        testResultsListener.reset();

        String inputTopic = System.getenv().getOrDefault("KAFKA_INPUT_TOPIC", "topic-file-upload");
        String key = "e2e-key-" + System.currentTimeMillis();
        String value = "e2e-value-" + System.currentTimeMillis();

        // Send message using GenericProducerImpl
        genericProducerImpl.send(inputTopic, key, value).get(10, TimeUnit.SECONDS);

        // Wait for GenericConsumerImpl to process and send result to topic-processed-results
        // TestResultsListener will capture the result
        boolean resultReceived = testResultsListener.waitForResult(15);
        assertThat(resultReceived).isTrue();

        String result = testResultsListener.getLastResult();
        assertThat(result).isNotNull();
        assertThat(result).contains(value);
        assertThat(result).contains("file upload switch case executed");
    }
}
