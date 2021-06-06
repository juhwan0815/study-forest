package com.study.authservice.kafka.producer;

import com.study.authservice.kafka.config.KafkaConfig;
import com.study.authservice.kafka.message.LogoutMessage;
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
public class KafkaLogoutProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "logoutProducerFactory")
    public ProducerFactory<String, LogoutMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "logoutKafkaTemplate")
    public KafkaTemplate<String,LogoutMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
