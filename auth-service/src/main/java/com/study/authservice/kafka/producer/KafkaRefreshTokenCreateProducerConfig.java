package com.study.authservice.kafka.producer;

import com.study.authservice.kafka.config.KafkaConfig;
import com.study.authservice.kafka.message.RefreshTokenCreateMessage;
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
public class KafkaRefreshTokenCreateProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "refreshTokenCreateProducerFactory")
    public ProducerFactory<String, RefreshTokenCreateMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "refreshTokenCreateKafkaTemplate")
    public KafkaTemplate<String,RefreshTokenCreateMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
