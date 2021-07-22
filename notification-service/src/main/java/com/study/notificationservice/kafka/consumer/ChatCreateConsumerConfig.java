package com.study.notificationservice.kafka.consumer;

import com.study.notificationservice.kafka.config.KafkaConfig;
import com.study.notificationservice.kafka.message.ChatCreateMessage;
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
public class ChatCreateConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.chat.create}")
    private String groupName;

    @Bean(name = "chatCreateConsumerFactory")
    public ConsumerFactory<String, ChatCreateMessage> chatCreateConsumerFactory(){
        JsonDeserializer<ChatCreateMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "chatCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,ChatCreateMessage> chatCreateListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , ChatCreateMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(chatCreateConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "chatCreateListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<ChatCreateMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<ChatCreateMessage> JsonDeserializer() {
        JsonDeserializer<ChatCreateMessage> deserializer = new JsonDeserializer<>(ChatCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
