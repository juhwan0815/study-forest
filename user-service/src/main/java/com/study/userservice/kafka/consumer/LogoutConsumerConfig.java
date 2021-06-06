package com.study.userservice.kafka.consumer;

import com.study.userservice.kafka.config.KafkaConfig;
import com.study.userservice.kafka.message.LogoutMessage;
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
public class LogoutConsumerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.consumer.logout.groupName}")
    private String groupName;

    @Bean(name = "logoutConsumerFactory")
    public ConsumerFactory<String, LogoutMessage> logoutConsumerFactory(){
        JsonDeserializer<LogoutMessage> deserializer = jsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(jsonDeserializer()),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "kafkaLogoutListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,LogoutMessage> kafkaLogoutListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,LogoutMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(logoutConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "kafkaLogoutListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<LogoutMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    private JsonDeserializer<LogoutMessage> jsonDeserializer() {
        JsonDeserializer<LogoutMessage> deserializer
                = new JsonDeserializer<>(LogoutMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }


}
