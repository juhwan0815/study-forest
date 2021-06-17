package com.study.userservice.kafka.sender;

import com.study.userservice.kafka.message.UserDeleteMessage;

public interface UserDeleteMessageSender {

    void send(UserDeleteMessage userDeleteMessage);
}
