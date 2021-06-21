package com.study.userservice.kafka.listener;

import com.study.userservice.kafka.message.StudyApplyCreateMessage;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StudyApplyCreateListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.studyApply.create}",
            groupId = "${kafka.consumer.studyApply.create}",
            containerFactory = "studyApplyCreateListenerContainerFactory")
    public void studyApplyCreateListen(@Payload StudyApplyCreateMessage studyApplyCreateMessage,
                                       @Headers MessageHeaders messageHeaders){
        userService.createStudyApply(studyApplyCreateMessage);
    }
}
