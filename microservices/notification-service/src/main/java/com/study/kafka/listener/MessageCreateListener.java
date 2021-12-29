package com.study.kafka.listener;

import com.study.kakfa.MessageCreateMessage;
import com.study.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageCreateListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.message.create}",
            groupId = "${kafka.consumer.message.create}",
            containerFactory = "messageCreateListenerContainerFactory")
    public void messageCreateListen(@Payload MessageCreateMessage messageCreateMessage,
                                    @Headers MessageHeaders messageHeaders) {
        notificationService.messageCreate(messageCreateMessage);
    }
}
