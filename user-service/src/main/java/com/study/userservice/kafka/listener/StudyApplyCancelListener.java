package com.study.userservice.kafka.listener;


import com.study.userservice.kafka.message.StudyApplyCancelMessage;
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
public class StudyApplyCancelListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.studyApply.cancel}",
            groupId = "${kafka.consumer.studyApply.cancel}",
            containerFactory = "studyApplyCancelListenerContainerFactory")
    public void studyApplyCancelListen(@Payload StudyApplyCancelMessage studyApplyCancelMessage,
                                       @Headers MessageHeaders messageHeaders){
        userService.cancelStudyApply(studyApplyCancelMessage);
    }
}
