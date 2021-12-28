package com.study.kafka.sender;

import com.study.kakfa.MessageCreateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageCreateSenderImpl implements MessageCreateSender {

    @Qualifier("messageCreateKafkaTemplate")
    private final KafkaTemplate<String, MessageCreateMessage> kafkaTemplate;

    @Value("${kafka.topic.message.create}")
    private String topic;

    @Override
    public void send(MessageCreateMessage messageCreateMessage) {
        Message<MessageCreateMessage> message = MessageBuilder
                .withPayload(messageCreateMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
    }
}
