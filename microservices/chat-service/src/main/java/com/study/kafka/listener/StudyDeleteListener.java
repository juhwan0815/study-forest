package com.study.kafka.listener;

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
public class StudyDeleteListener {

    private final MessageService messageService;

    @KafkaListener(topics = "${kafka.topic.study.delete}",
            groupId = "${kafka.consumer.study.delete}",
            containerFactory = "studyDeleteListenerContainerFactory")
    public void studyDeleteListen(@Payload StudyDeleteMessage studyDeleteMessage,
                                  @Headers MessageHeaders messageHeaders) {
        messageService.deleteMessage(studyDeleteMessage);
    }
}
