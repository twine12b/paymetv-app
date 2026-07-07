package com.paymetv.app.service.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:127.0.0.1:9092}"})
public class GenericProducerIntegrationTest {

    @Autowired
    private GenericProducer genericProducer;

    @Autowired
    private GenericConsumer genericConsumer; // ensure listener bean is present and registered

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; // overridden in TestKafkaConfig

    @TestConfiguration
    static class TestKafkaConfig {

        @Bean
        @Primary
        public ProducerFactory<String, Object> producerFactory() {
            String brokers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "127.0.0.1:9092");
            Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        @Primary
        public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
            return new KafkaTemplate<>(pf);
        }
    }

    @Test
    public void e2eProducerToConsumerUsingRealKafka() throws Exception {
        String brokers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "127.0.0.1:9092");
        String inputTopic = System.getenv().getOrDefault("KAFKA_INPUT_TOPIC", "topic-file-upload");
        String resultsTopic = System.getenv().getOrDefault("KAFKA_RESULTS_TOPIC", "topic-processed-results");

        // create a consumer to read processed results
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("e2e-res-group-" + System.currentTimeMillis(), "true", brokers);
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        Consumer<String, String> consumer = cf.createConsumer();
        consumer.subscribe(java.util.Collections.singletonList(resultsTopic));
        // ensure group join
        consumer.poll(Duration.ofMillis(200));

        String key = "e2e-key-" + System.currentTimeMillis();
        String value = "e2e-value-" + System.currentTimeMillis();

        // send using GenericProducer (application's kafkaTemplate will be the overridden one pointing to real Kafka)
        genericProducer.send(inputTopic, key, value).get(10, TimeUnit.SECONDS);

        // poll for processed message
        boolean found = false;
        long end = System.currentTimeMillis() + 20000;
        while (System.currentTimeMillis() < end && !found) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> r : records) {
                String v = r.value();
                if (v != null && v.contains(value) && v.contains("file upload switch case executed")) {
                    found = true;
                    break;
                }
            }
        }

        consumer.close();
        assertThat(found).isTrue();
    }
}
