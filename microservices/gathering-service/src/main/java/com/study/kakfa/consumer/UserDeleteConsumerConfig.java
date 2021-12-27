package com.study.kakfa.consumer;

import com.study.kakfa.StudyDeleteMessage;
import com.study.kakfa.UserDeleteMessage;
import com.study.kakfa.config.KafkaConfig;
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
public class UserDeleteConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.user.delete}")
    private String groupName;

    @Bean(name = "userDeleteConsumerFactory")
    public ConsumerFactory<String, UserDeleteMessage> userDeleteConsumerFactory(){
        JsonDeserializer<UserDeleteMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "userDeleteListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,UserDeleteMessage> userDeleteListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , UserDeleteMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(userDeleteConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "userDeleteListenerContainerFactory")
    private Map<String,Object> consumerFactoryConfig(JsonDeserializer<UserDeleteMessage> deserializer){
        return KafkaConfig.consumerFactoryConfig(kafkaServer,groupName,deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<UserDeleteMessage> JsonDeserializer() {
        JsonDeserializer<UserDeleteMessage> deserializer = new JsonDeserializer<>(UserDeleteMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
