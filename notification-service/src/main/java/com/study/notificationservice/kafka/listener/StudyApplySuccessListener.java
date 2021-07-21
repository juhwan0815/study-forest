package com.study.notificationservice.kafka.listener;

import com.study.notificationservice.kafka.message.StudyApplySuccessMessage;
import com.study.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyApplySuccessListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.studyApply.success}",
            groupId = "${kafka.consumer.studyApply.success}",
            containerFactory = "studyApplySuccessListenerContainerFactory")
    public void studyApplySuccessListen(@Payload StudyApplySuccessMessage studyApplySuccessMessage,
                                       @Headers MessageHeaders messageHeaders){
        notificationService.studyApplySuccess(studyApplySuccessMessage);
    }
}
