package com.study.kafka.consumer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.MessageCreateMessage;
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
public class MessageCreateConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.message.create}")
    private String groupName;

    @Bean(name = "messageCreateConsumerFactory")
    public ConsumerFactory<String, MessageCreateMessage> messageCreateConsumerFactory() {
        JsonDeserializer<MessageCreateMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "messageCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MessageCreateMessage> messageCreateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageCreateMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(messageCreateConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "messageCreateListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<MessageCreateMessage> deserializer) {
        return KafkaConfig.consumerFactoryConfig(kafkaServer, groupName, deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<MessageCreateMessage> JsonDeserializer() {
        JsonDeserializer<MessageCreateMessage> deserializer = new JsonDeserializer<>(MessageCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
