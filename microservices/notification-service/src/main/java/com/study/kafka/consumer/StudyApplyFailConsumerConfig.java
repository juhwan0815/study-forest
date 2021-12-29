package com.study.kafka.consumer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.StudyApplyFailMessage;
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
public class StudyApplyFailConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.studyApply.fail}")
    private String groupName;


    @Bean(name = "studyApplyFailConsumerFactory")
    public ConsumerFactory<String, StudyApplyFailMessage> studyApplyFailConsumerFactory(){
        JsonDeserializer<StudyApplyFailMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "studyApplyFailListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,StudyApplyFailMessage> studyApplyFailListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , StudyApplyFailMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(studyApplyFailConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "studyApplyFailListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<StudyApplyFailMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<StudyApplyFailMessage> JsonDeserializer() {
        JsonDeserializer<StudyApplyFailMessage> deserializer = new JsonDeserializer<>(StudyApplyFailMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
