package com.study.kafka.consumer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.StudyDeleteMessage;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
public class StudyDeleteConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.study.delete}")
    private String groupName;

    @Bean(name = "studyDeleteConsumerFactory")
    public ConsumerFactory<String, StudyDeleteMessage> studyDeleteConsumerFactory() {
        JsonDeserializer<StudyDeleteMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "studyDeleteListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, StudyDeleteMessage> studyDeleteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StudyDeleteMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(studyDeleteConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "studyDeleteListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<StudyDeleteMessage> deserializer) {
        return KafkaConfig.consumerFactoryConfig(kafkaServer, groupName, deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<StudyDeleteMessage> JsonDeserializer() {
        JsonDeserializer<StudyDeleteMessage> deserializer = new JsonDeserializer<>(StudyDeleteMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
