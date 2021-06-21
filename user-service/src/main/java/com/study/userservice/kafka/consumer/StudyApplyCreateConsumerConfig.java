package com.study.userservice.kafka.consumer;

import com.study.userservice.kafka.config.KafkaConfig;
import com.study.userservice.kafka.message.StudyApplyCreateMessage;
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
public class StudyApplyCreateConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.studyApply.create}")
    private String groupName;


    @Bean(name = "studyApplyCreateConsumerFactory")
    public ConsumerFactory<String, StudyApplyCreateMessage> studyApplyCreateConsumerFactory(){
        JsonDeserializer<StudyApplyCreateMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "studyApplyCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,StudyApplyCreateMessage> studyApplyCreateListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , StudyApplyCreateMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(studyApplyCreateConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "studyApplyCreateListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<StudyApplyCreateMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<StudyApplyCreateMessage> JsonDeserializer() {
        JsonDeserializer<StudyApplyCreateMessage> deserializer = new JsonDeserializer<>(StudyApplyCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
