package com.paymetv.app.service.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = com.paymetv.app.AppApplication.class)
public class GenericProducerIntegrationTest {

    @Autowired
    private GenericProducer genericProducer;

    @TestConfiguration
    static class TestKafkaConfig {

        @Bean
        @Primary
        public ProducerFactory<String, Object> producerFactory() {
            Map<String, Object> config = new HashMap<>();
            String brokers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "127.0.0.1:9092");
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            // use String serializer for end-to-end test so consumer can read as String
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        @Primary
        public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }
    }

    @Test
    public void testSendAndConsumeWithRealKafka() throws Exception {
        String brokers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "127.0.0.1:9092");
        String topic = System.getenv().getOrDefault("KAFKA_TEST_TOPIC", "test-topic");

        // create topic if it does not exist
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers))) {
            NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
            try {
                admin.createTopics(List.of(newTopic)).all().get(5, TimeUnit.SECONDS);
            } catch (Exception ignore) {
                // topic may already exist; ignore failures
            }
        }

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "e2e-test-group-" + System.currentTimeMillis());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new StringDeserializer());

        Consumer<String, String> consumer = cf.createConsumer();
        consumer.subscribe(Collections.singletonList(topic));
        // allow group join and assignment
        consumer.poll(Duration.ofMillis(100));
        consumer.seekToBeginning(consumer.assignment());

        String key = "test-key-" + System.currentTimeMillis();
        String value = "test-value-" + System.currentTimeMillis();

        // send raw String payload (producer uses StringSerializer in this test)
        genericProducer.send(topic, key, value).get(10, TimeUnit.SECONDS);

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records.count()).isGreaterThan(0);

        boolean found = false;
        for (ConsumerRecord<String, String> record : records) {
            if (key.equals(record.key()) && value.equals(record.value())) {
                found = true;
                break;
            }
        }

        consumer.close();
        assertThat(found).isTrue();
    }
}
