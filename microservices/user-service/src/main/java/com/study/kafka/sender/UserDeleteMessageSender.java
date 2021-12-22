package com.study.kafka.sender;

import com.study.kakfa.UserDeleteMessage;

public interface UserDeleteMessageSender {

    void send(UserDeleteMessage userDeleteMessage);
}
