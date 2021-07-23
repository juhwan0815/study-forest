package com.study.chatservice.kafka.listener;

import com.study.chatservice.kafka.message.StudyDeleteMessage;
import com.study.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyDeleteListener {

    private final ChatRoomService chatRoomService;

    @KafkaListener(topics = "${kafka.topic.study.delete}",
            groupId = "${kafka.consumer.study.delete}",
            containerFactory = "studyDeleteListenerContainerFactory")
    public void studyDeleteListen(@Payload StudyDeleteMessage studyDeleteMessage,
                                  @Headers MessageHeaders messageHeaders) {
        chatRoomService.deleteChatRoomsAndChatMessages(studyDeleteMessage);
    }
}
