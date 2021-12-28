package com.study.kafka.sender;

import com.study.kakfa.MessageCreateMessage;

public interface MessageCreateSender {

    void send(MessageCreateMessage messageCreateMessage);
}
