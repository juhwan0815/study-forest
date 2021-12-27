package com.study.kakfa.listener;

import com.study.kakfa.StudyDeleteMessage;
import com.study.service.GatheringService;
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
