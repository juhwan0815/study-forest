package com.study.userservice.kafka.listener;

import com.study.userservice.kafka.message.LogoutMessage;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLogoutListener {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.logout.name}",
            groupId = "${kafka.consumer.logout.groupName}",
            containerFactory = "kafkaLogoutListenerContainerFactory")
    public void logoutListen(@Payload LogoutMessage logoutMessage,
                             @Headers MessageHeaders messageHeaders) {
        userService.logout(logoutMessage);
    }
}
