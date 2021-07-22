package com.study.notificationservice.kafka.listener;

import com.study.notificationservice.kafka.message.ChatCreateMessage;
import com.study.notificationservice.kafka.message.StudyCreateMessage;
import com.study.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatCreateListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.chat.create}",
            groupId = "${kafka.consumer.chat.create}",
            containerFactory = "chatCreateListenerContainerFactory")
    public void chatCreateListen(@Payload ChatCreateMessage chatCreateMessage,
                                  @Headers MessageHeaders messageHeaders) {
        notificationService.chatCreate(chatCreateMessage);
    }
}
