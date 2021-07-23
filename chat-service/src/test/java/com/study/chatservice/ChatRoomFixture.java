package com.study.chatservice;

import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.model.chatroom.ChatRoomUpdateRequest;

public class ChatRoomFixture {

    public static final ChatRoomCreateRequest TEST_CHAT_ROOM_CREATE_REQUEST = new ChatRoomCreateRequest("공지사항");

    public static final ChatRoomUpdateRequest TEST_CHAT_ROOM_UPDATE_REQUEST = new ChatRoomUpdateRequest("공지사항");

    public static final ChatRoomResponse TEST_CHAT_ROOM_RESPONSE = new ChatRoomResponse(1L,"공지사항",1L);

    public static final ChatRoom TEST_CHAT_ROOM = new ChatRoom(1L,"공지사항",1L);

    public static ChatRoom createTestChatRoom(){
        return new ChatRoom(1L,"공지사항",1L);
    }
}
