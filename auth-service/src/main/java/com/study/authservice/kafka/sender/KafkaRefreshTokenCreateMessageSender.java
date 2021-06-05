package com.study.authservice.kafka.sender;

import com.study.authservice.kafka.message.RefreshTokenCreateMessage;

public interface KafkaRefreshTokenCreateMessageSender {

    void send(RefreshTokenCreateMessage refreshTokenCreateMessage);
}
