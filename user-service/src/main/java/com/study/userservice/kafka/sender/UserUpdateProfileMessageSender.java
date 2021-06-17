package com.study.userservice.kafka.sender;

import com.study.userservice.kafka.message.UserUpdateProfileMessage;

public interface UserUpdateProfileMessageSender {

    void send(UserUpdateProfileMessage userUpdateProfileMessage);
}
