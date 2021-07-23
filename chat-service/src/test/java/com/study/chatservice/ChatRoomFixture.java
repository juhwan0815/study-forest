package com.study.chatservice;

import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;

public class ChatRoomFixture {

    public static final ChatRoomCreateRequest TEST_CHAT_ROOM_CREATE_REQUEST = new ChatRoomCreateRequest("공지사항");

    public static final ChatRoomResponse TEST_CHAT_ROOM_RESPONSE = new ChatRoomResponse(1L,"공지사항",1L);
}
