package com.study.userservice.kafka.listener;

import com.study.userservice.kafka.message.StudyApplyFailMessage;
import com.study.userservice.kafka.message.StudyApplySuccessMessage;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyApplyFailListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.studyApply.fail}",
            groupId = "${kafka.consumer.studyApply.fail}",
            containerFactory = "studyApplyFailListenerContainerFactory")
    public void studyApplyFailListen(@Payload StudyApplyFailMessage studyApplyFailMessage,
                                     @Headers MessageHeaders messageHeaders) {
        userService.FailStudyApply(studyApplyFailMessage);
    }
}
