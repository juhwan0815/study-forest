package com.study.kakfa.sender;

import com.study.kakfa.ChatRoomDeleteMessage;
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
public class ChatRoomDeleteMessageSenderImpl implements ChatRoomDeleteMessageSender{

    @Qualifier("chatRoomDeleteKafkaTemplate")
    private final KafkaTemplate<String, ChatRoomDeleteMessage> kafkaTemplate;

    @Value("${kafka.topic.chatRoom.delete}")
    private String topic;

    @Override
    public void send(ChatRoomDeleteMessage chatRoomDeleteMessage) {
        Message<ChatRoomDeleteMessage> message = MessageBuilder
                .withPayload(chatRoomDeleteMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
    }
}
