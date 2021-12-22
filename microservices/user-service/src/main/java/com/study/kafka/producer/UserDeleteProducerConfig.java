package com.study.kafka.producer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.UserDeleteMessage;
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
public class UserDeleteProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "userDeleteProducerFactory")
    public ProducerFactory<String, UserDeleteMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "userDeleteKafkaTemplate")
    public KafkaTemplate<String,UserDeleteMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
}
