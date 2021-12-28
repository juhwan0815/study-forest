package com.study.kafka.consumer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.ChatRoomDeleteMessage;
import com.study.kakfa.StudyDeleteMessage;
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
public class ChatRoomDeleteConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.chatRoom.delete}")
    private String groupName;

    @Bean(name = "chatRoomDeleteConsumerFactory")
    public ConsumerFactory<String, ChatRoomDeleteMessage> chatRoomDeleteConsumerFactory() {
        JsonDeserializer<ChatRoomDeleteMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "chatRoomDeleteListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ChatRoomDeleteMessage> chatRoomDeleteListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatRoomDeleteMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(chatRoomDeleteConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "chatRoomDeleteListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<ChatRoomDeleteMessage> deserializer) {
        return KafkaConfig.consumerFactoryConfig(kafkaServer, groupName, deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<ChatRoomDeleteMessage> JsonDeserializer() {
        JsonDeserializer<ChatRoomDeleteMessage> deserializer = new JsonDeserializer<>(ChatRoomDeleteMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
