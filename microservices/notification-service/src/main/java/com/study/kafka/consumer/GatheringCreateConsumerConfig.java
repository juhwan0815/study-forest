package com.study.kafka.consumer;

import com.study.kafka.config.KafkaConfig;
import com.study.kakfa.GatheringCreateMessage;
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
public class GatheringCreateConsumerConfig {

    // 카프카 서버
    @Value("${kafka.server}")
    private String kafkaServer;

    // 그룹
    @Value("${kafka.consumer.gathering.create}")
    private String groupName;

    @Bean(name = "gatheringCreateConsumerFactory")
    public ConsumerFactory<String, GatheringCreateMessage> gatheringCreateConsumerFactory() {
        JsonDeserializer<GatheringCreateMessage> deserializer = JsonDeserializer();

        return new DefaultKafkaConsumerFactory<>(
                consumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    @Bean(name = "gatheringCreateListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, GatheringCreateMessage> gatheringCreateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GatheringCreateMessage> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        kafkaListenerContainerFactory.setConsumerFactory(gatheringCreateConsumerFactory());
        return kafkaListenerContainerFactory;
    }

    @ConditionalOnMissingBean(name = "gatheringCreateListenerContainerFactory")
    private Map<String, Object> consumerFactoryConfig(JsonDeserializer<GatheringCreateMessage> deserializer) {
        return KafkaConfig.consumerFactoryConfig(kafkaServer, groupName, deserializer);
    }

    // JSON DeSerializaer 설정
    private JsonDeserializer<GatheringCreateMessage> JsonDeserializer() {
        JsonDeserializer<GatheringCreateMessage> deserializer = new JsonDeserializer<>(GatheringCreateMessage.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
