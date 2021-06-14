package com.study.userservice.kafka.consumer;

import com.study.userservice.kafka.config.KafkaConfig;
import com.study.userservice.kafka.message.LogoutMessage;
import com.study.userservice.kafka.message.StudyJoinMessage;
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
public class StudyJoinConsumerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.consumer.logout.groupName}")
    private String groupName;

    @Bean(name = "studyJoinConsumerFactory")
    public ConsumerFactory<String, StudyJoinMessage> studyJoinConsumerFactory(){
        JsonDeserializer<StudyJoinMessage> deserializer = jsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(jsonDeserializer()),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaStudyJoinListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,StudyJoinMessage> kafkaStudyJoinListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,StudyJoinMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(studyJoinConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "kafkaStudyJoinListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<StudyJoinMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    private JsonDeserializer<StudyJoinMessage> jsonDeserializer() {
        JsonDeserializer<StudyJoinMessage> deserializer
                = new JsonDeserializer<>(StudyJoinMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
