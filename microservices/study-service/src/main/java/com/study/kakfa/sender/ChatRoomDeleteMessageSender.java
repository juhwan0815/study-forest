package com.study.kakfa.sender;

import com.study.kakfa.ChatRoomDeleteMessage;

public interface ChatRoomDeleteMessageSender {

    void send(ChatRoomDeleteMessage chatRoomDeleteMessage);
}
