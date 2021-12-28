package com.study.kafka.producer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.MessageCreateMessage;
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
public class MessageCreateProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "messageCreateProducerFactory")
    public ProducerFactory<String, MessageCreateMessage> producerFactory() {
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "messageCreateKafkaTemplate")
    public KafkaTemplate<String, MessageCreateMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
