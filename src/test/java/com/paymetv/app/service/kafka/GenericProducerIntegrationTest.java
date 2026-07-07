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
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"topic-file-upload", "topic-processed-results"})
public class GenericProducerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private GenericProducer genericProducer;

    @Autowired
    private GenericConsumer genericConsumer; // ensures listener bean is created

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; // wired to embedded broker via TestKafkaConfig

    @TestConfiguration
    static class TestKafkaConfig {

        @Bean
        @Primary
        public ProducerFactory<String, Object> producerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
            Map<String, Object> props = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaProducerFactory<>(props);
        }

        @Bean
        @Primary
        public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
            return new KafkaTemplate<>(pf);
        }

        @Bean
        @Primary
        public DefaultKafkaConsumerFactory<String, String> consumerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
            Map<String, Object> consumerProps = new HashMap<>(KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker));
            consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        }

        @Bean
        @Primary
        public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(DefaultKafkaConsumerFactory<String, String> cf) {
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(cf);
            factory.setConcurrency(1);
            return factory;
        }
    }

    @Test
    public void testEndToEndUsingGenericProducer() throws Exception {
        String inputTopic = "topic-file-upload";
        String resultsTopic = "topic-processed-results";

        Map<String, Object> consumerProps = new HashMap<>(KafkaTestUtils.consumerProps("resGroup", "true", embeddedKafkaBroker));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        Consumer<String, String> consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, resultsTopic);

        String key = "e2e-key-" + System.currentTimeMillis();
        String value = "e2e-value-" + System.currentTimeMillis();

        genericProducer.send(inputTopic, key, value).get(10, TimeUnit.SECONDS);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(15));
        assertThat(records.count()).isGreaterThan(0);

        boolean found = false;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value() != null && record.value().contains(value) && record.value().contains("file upload switch case executed")) {
                found = true;
                break;
            }
        }

        consumer.close();
        assertThat(found).isTrue();
    }

    @Test
    public void testEndToEndUsingKafkaTemplateAndGenericConsumer() throws Exception {
        String inputTopic = "topic-file-upload";
        String resultsTopic = "topic-processed-results";

        Map<String, Object> consumerProps = new HashMap<>(KafkaTestUtils.consumerProps("resGroup2", "true", embeddedKafkaBroker));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        Consumer<String, String> consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, resultsTopic);

        String key = "e2e2-key-" + System.currentTimeMillis();
        String value = "e2e2-value-" + System.currentTimeMillis();

        // send via kafkaTemplate to simulate external producer
        kafkaTemplate.send(inputTopic, key, value).get(10, TimeUnit.SECONDS);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(15));
        assertThat(records.count()).isGreaterThan(0);

        boolean found = false;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value() != null && record.value().contains(value) && record.value().contains("file upload switch case executed")) {
                found = true;
                break;
            }
        }

        consumer.close();
        assertThat(found).isTrue();
    }
}
