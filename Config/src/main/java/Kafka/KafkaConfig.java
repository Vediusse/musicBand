package Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    private Map<String, Object> consumerConfig(String groupId, Class<?> valueType) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return config;
    }

    private <T> ProducerFactory<String, T> producerFactory(Class<T> valueType) {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    private <T> ConsumerFactory<String, T> consumerFactory(String groupId, Class<T> valueType) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(groupId, valueType));
    }

    @Bean
    public KafkaTemplate<String, BandRegisteredEvent> bandRegisteredEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(BandRegisteredEvent.class));
    }

    @Bean
    public KafkaTemplate<String, RoleCheckResponse> roleCheckResponseKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(RoleCheckResponse.class));
    }

    @Bean
    public KafkaTemplate<String, RoleCheckRequest> roleCheckRequestKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(RoleCheckRequest.class));
    }

    @Bean
    public KafkaTemplate<String, EventRegistrationResponse> eventResponseKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(EventRegistrationResponse.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BandRegisteredEvent> bandRegisteredEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BandRegisteredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("event-service-group", BandRegisteredEvent.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventRegistrationResponse> bandResponseEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EventRegistrationResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("event-service-group", EventRegistrationResponse.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoleCheckRequest> roleCheckRequestKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoleCheckRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("auth", RoleCheckRequest.class));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RoleCheckResponse> roleCheckResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RoleCheckResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory("auth", RoleCheckResponse.class));
        return factory;
    }
}
