package com.study.kafka.listener;

import com.study.kakfa.ChatRoomDeleteMessage;
import com.study.kakfa.StudyDeleteMessage;
import com.study.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomDeleteListener {

    private final MessageService messageService;

    @KafkaListener(topics = "${kafka.topic.chatRoom.delete}",
            groupId = "${kafka.consumer.chatRoom.delete}",
            containerFactory = "chatRoomDeleteListenerContainerFactory")
    public void chatRoomDeleteListen(@Payload ChatRoomDeleteMessage chatRoomDeleteMessage,
                                  @Headers MessageHeaders messageHeaders) {
        messageService.deleteMessage(chatRoomDeleteMessage);
    }
}
