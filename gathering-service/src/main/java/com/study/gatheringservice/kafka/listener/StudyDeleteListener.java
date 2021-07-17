package com.study.gatheringservice.kafka.listener;

import com.study.gatheringservice.domain.GatheringUser;
import com.study.gatheringservice.kafka.message.StudyDeleteMessage;
import com.study.gatheringservice.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyDeleteListener {

    private final GatheringService gatheringService;

    @KafkaListener(topics = "${kafka.topic.study.delete}",
            groupId = "${kafka.consumer.study.delete}",
            containerFactory = "studyDeleteListenerContainerFactory")
    public void studyDeleteListen(@Payload StudyDeleteMessage studyDeleteMessage,
                                  @Headers MessageHeaders messageHeaders) {
        gatheringService.deleteByStudyId(studyDeleteMessage);
    }
}
