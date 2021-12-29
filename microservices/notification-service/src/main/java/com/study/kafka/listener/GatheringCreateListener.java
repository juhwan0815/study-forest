package com.study.kafka.listener;

import com.study.kakfa.GatheringCreateMessage;
import com.study.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GatheringCreateListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.gathering.create}",
            groupId = "${kafka.consumer.gathering.create}",
            containerFactory = "gatheringCreateListenerContainerFactory")
    public void gatheringCreateListen(@Payload GatheringCreateMessage gatheringCreateMessage,
                                      @Headers MessageHeaders messageHeaders) {
        notificationService.gatheringCreate(gatheringCreateMessage);
    }
}
