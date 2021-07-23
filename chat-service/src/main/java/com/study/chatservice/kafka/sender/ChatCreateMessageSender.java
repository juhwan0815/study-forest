package com.study.chatservice.kafka.sender;


import com.study.chatservice.kafka.message.ChatCreateMessage;

public interface ChatCreateMessageSender {

    void send(ChatCreateMessage chatCreateMessage);
}
