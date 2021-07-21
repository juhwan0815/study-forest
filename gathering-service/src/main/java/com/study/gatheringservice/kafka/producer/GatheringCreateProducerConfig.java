package com.study.gatheringservice.kafka.producer;

import com.study.gatheringservice.kafka.config.KafkaConfig;
import com.study.gatheringservice.kafka.message.GatheringCreateMessage;
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
public class GatheringCreateProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "gatheringCreateProducerFactory")
    public ProducerFactory<String, GatheringCreateMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "gatheringCreateKafkaTemplate")
    public KafkaTemplate<String, GatheringCreateMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}