package com.study.userservice.kafka.listener;

import com.study.userservice.kafka.message.StudyApplyCreateMessage;
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
public class StudyApplySuccessListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.studyApply.success}",
            groupId = "${kafka.consumer.studyApply.success}",
            containerFactory = "studyApplySuccessListenerContainerFactory")
    public void studyApplySuccessListen(@Payload StudyApplySuccessMessage studyApplySuccessMessage,
                                       @Headers MessageHeaders messageHeaders){
        userService.SuccessStudyApply(studyApplySuccessMessage);
    }
}
