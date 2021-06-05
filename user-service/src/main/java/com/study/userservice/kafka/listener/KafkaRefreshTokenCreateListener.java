package com.study.userservice.kafka.listener;

import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaRefreshTokenCreateListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.refresh.name}",
            groupId = "${kafka.consumer.refresh.groupName}",
            containerFactory = "kafkaRefreshTokenCreateListenerContainerFactory")
    public void refreshTokenCreateListen(@Payload RefreshTokenCreateMessage refreshTokenCreateMessage,
                                         @Headers MessageHeaders messageHeaders) {
        userService.updateRefreshToken(refreshTokenCreateMessage);
    }
}
