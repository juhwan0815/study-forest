package com.study.notificationservice.kafka.consumer;

import com.study.notificationservice.kafka.config.KafkaConfig;
import com.study.notificationservice.kafka.message.StudyApplySuccessMessage;
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
public class StudyApplySuccessConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.studyApply.success}")
    private String groupName;


    @Bean(name = "studyApplySuccessConsumerFactory")
    public ConsumerFactory<String, StudyApplySuccessMessage> studyApplySuccessConsumerFactory(){
        JsonDeserializer<StudyApplySuccessMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "studyApplySuccessListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,StudyApplySuccessMessage> studyApplySuccessListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , StudyApplySuccessMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(studyApplySuccessConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "studyApplySuccessListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<StudyApplySuccessMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<StudyApplySuccessMessage> JsonDeserializer() {
        JsonDeserializer<StudyApplySuccessMessage> deserializer = new JsonDeserializer<>(StudyApplySuccessMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
