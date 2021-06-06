package com.study.authservice.kafka.sender;

import com.study.authservice.kafka.message.LogoutMessage;

public interface KafkaLogoutMessageSender {

    void send(LogoutMessage logoutMessage);
}
