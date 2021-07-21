package com.study.notificationservice.kafka.listener;

import com.study.notificationservice.kafka.message.StudyApplyFailMessage;
import com.study.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyApplyFailListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.studyApply.fail}",
            groupId = "${kafka.consumer.studyApply.fail}",
            containerFactory = "studyApplyFailListenerContainerFactory")
    public void studyApplyFailListen(@Payload StudyApplyFailMessage studyApplyFailMessage,
                                     @Headers MessageHeaders messageHeaders) {
        notificationService.studyApplyFail(studyApplyFailMessage);
    }
}
