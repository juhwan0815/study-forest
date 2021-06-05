package com.study.userservice.kafka.consumer;

import com.study.userservice.kafka.config.KafkaConfig;
import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
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
public class RefreshTokenCreateConsumerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.consumer.refresh.groupName}")
    private String groupName;

    @Bean(name = "refreshTokenCreateConsumerFactory")
    public ConsumerFactory<String, RefreshTokenCreateMessage> refreshTokenCreateConsumerFactory(){
        JsonDeserializer<RefreshTokenCreateMessage> deserializer = jsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(jsonDeserializer()),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaRefreshTokenCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,RefreshTokenCreateMessage> kafkaRefreshTokenCreateListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,RefreshTokenCreateMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(refreshTokenCreateConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "kafkaRefreshTokenCreateListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<RefreshTokenCreateMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    private JsonDeserializer<RefreshTokenCreateMessage> jsonDeserializer() {
        JsonDeserializer<RefreshTokenCreateMessage> deserializer
                = new JsonDeserializer<>(RefreshTokenCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
