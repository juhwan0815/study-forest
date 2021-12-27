package com.study.kakfa.sender;

import com.study.kakfa.GatheringCreateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatheringCreateMessageSenderImpl implements GatheringCreateMessageSender {

    @Qualifier("gatheringCreateKafkaTemplate")
    private final KafkaTemplate<String, GatheringCreateMessage> kafkaTemplate;

    @Value("${kafka.topic.gathering.create}")
    private String topic;

    @Override
    public void send(GatheringCreateMessage gatheringCreateMessage) {
        Message<GatheringCreateMessage> message = MessageBuilder
                .withPayload(gatheringCreateMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
    }
}
