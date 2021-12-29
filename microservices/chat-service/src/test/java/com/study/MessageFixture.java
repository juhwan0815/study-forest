package com.study;

import com.study.domain.Message;
import com.study.dto.MessageRequest;
import com.study.dto.MessageResponse;
import com.study.kakfa.MessageCreateMessage;

public class MessageFixture {

    public static final MessageRequest TEST_MESSAGE_REQUEST = new MessageRequest(1L, "안녕하세요");

    public static final Message TEST_MESSAGE = Message.createMessage(1L, "황주환", "안녕하세요", 1L);

    public static final MessageResponse TEST_MESSAGE_RESPONSE = MessageResponse.from(TEST_MESSAGE);
}
