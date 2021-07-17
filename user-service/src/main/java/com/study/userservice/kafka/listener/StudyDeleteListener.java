package com.study.userservice.kafka.listener;


import com.study.userservice.kafka.message.StudyApplyCancelMessage;
import com.study.userservice.kafka.message.StudyDeleteMessage;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyDeleteListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.study.delete}",
            groupId = "${kafka.consumer.study.delete}",
            containerFactory = "studyDeleteListenerContainerFactory")
    public void studyDeleteListen(@Payload StudyDeleteMessage studyDeleteMessage,
                                  @Headers MessageHeaders messageHeaders){
        userService.deleteStudyApplyByStudyId(studyDeleteMessage);
    }
}
