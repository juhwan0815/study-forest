package com.study.notificationservice.kafka.consumer;

import com.study.notificationservice.kafka.config.KafkaConfig;
import com.study.notificationservice.kafka.message.StudyApplySuccessMessage;
import com.study.notificationservice.kafka.message.StudyCreateMessage;
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
public class StudyCreateConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.study.create}")
    private String groupName;


    @Bean(name = "studyCreateConsumerFactory")
    public ConsumerFactory<String, StudyCreateMessage> studyCreateConsumerFactory(){
        JsonDeserializer<StudyCreateMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "studyCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,StudyCreateMessage> studyCreateListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , StudyCreateMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(studyCreateConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "studyCreateListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<StudyCreateMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<StudyCreateMessage> JsonDeserializer() {
        JsonDeserializer<StudyCreateMessage> deserializer = new JsonDeserializer<>(StudyCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
