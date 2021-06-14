package com.study.userservice.kafka.listener;

import com.study.userservice.kafka.message.StudyJoinMessage;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaStudyJoinMessageListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.study.join.name}",
    groupId = "${kafka.consumer.study.join.groupName}",
    containerFactory = "kafkaStudyJoinListenerContainerFactory")
    public void studyJoinMessageListen(@Payload StudyJoinMessage studyJoinMessage,
                                       @Headers MessageHeaders messageHeaders){
        userService.handleStudyJoin(studyJoinMessage);
    }

}
