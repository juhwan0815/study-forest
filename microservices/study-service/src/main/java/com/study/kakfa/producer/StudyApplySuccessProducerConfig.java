package com.study.kakfa.producer;

import com.study.kakfa.StudyApplySuccessMessage;
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
public class StudyApplySuccessProducerConfig {

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean(name = "studyApplySuccessProducerFactory")
    public ProducerFactory<String, StudyApplySuccessMessage> producerFactory(){
        Map<String, Object> properties = KafkaConfig.producerFactory(kafkaServer);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean(name = "studyApplySuccessKafkaTemplate")
    public KafkaTemplate<String, StudyApplySuccessMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
