package com.study.notificationservice.kafka.listener;

import com.study.notificationservice.kafka.message.StudyApplySuccessMessage;
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
public class StudyCreateListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.study.create}",
            groupId = "${kafka.consumer.study.create}",
            containerFactory = "studyCreateListenerContainerFactory")
    public void studyCreateListen(@Payload StudyCreateMessage studyCreateMessage,
                                  @Headers MessageHeaders messageHeaders) {
        notificationService.studyCreate(studyCreateMessage);
    }
}
