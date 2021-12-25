package com.study.kakfa.producer;

import com.study.kakfa.StudyDeleteMessage;
import com.study.kakfa.config.KafkaConfig;
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
public class StudyDeleteProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "studyDeleteProducerFactory")
    public ProducerFactory<String, StudyDeleteMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "studyDeleteKafkaTemplate")
    public KafkaTemplate<String,StudyDeleteMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
