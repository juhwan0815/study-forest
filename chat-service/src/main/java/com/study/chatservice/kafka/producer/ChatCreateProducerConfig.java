package com.study.chatservice.kafka.producer;

import com.study.chatservice.kafka.config.KafkaConfig;
import com.study.chatservice.kafka.message.ChatCreateMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@EnableKafka
@Configuration
public class ChatCreateProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "chatCreateProducerFactory")
    public ProducerFactory<String, ChatCreateMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "chatCreateKafkaTemplate")
    public KafkaTemplate<String,ChatCreateMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
