package com.study.chatservice;

import com.study.chatservice.domain.ChatMessage;
import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.kafka.listener.StudyDeleteListener;
import com.study.chatservice.kafka.message.StudyDeleteMessage;
import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.model.chatroom.ChatRoomUpdateRequest;

import java.time.LocalDateTime;

public class ChatRoomFixture {

    public static final ChatRoomCreateRequest TEST_CHAT_ROOM_CREATE_REQUEST = new ChatRoomCreateRequest("공지사항");

    public static final ChatRoomUpdateRequest TEST_CHAT_ROOM_UPDATE_REQUEST = new ChatRoomUpdateRequest("공지사항");

    public static final ChatRoomResponse TEST_CHAT_ROOM_RESPONSE = new ChatRoomResponse(1L,"공지사항",1L);
    public static final ChatRoomResponse TEST_CHAT_ROOM_RESPONSE2 = new ChatRoomResponse(2L,"토론방",1L);

    public static final ChatRoom TEST_CHAT_ROOM = new ChatRoom(1L,"공지사항",1L);
    public static final ChatRoom TEST_CHAT_ROOM2 = new ChatRoom(2L,"토론방",1L);

    public static final ChatMessage TEST_CHAT_MESSAGE1 = new ChatMessage(1L,"황주환","안녕하세요",1L, LocalDateTime.of(2021,7,23,10,0));
    public static final ChatMessage TEST_CHAT_MESSAGE2 = new ChatMessage(1L,"황주환","안녕하세요",1L, LocalDateTime.of(2021,7,23,10,1));
    public static final ChatMessage TEST_CHAT_MESSAGE3 = new ChatMessage(1L,"황주환","안녕하세요",1L, LocalDateTime.of(2021,7,23,10,2));

    public static final ChatMessageResponse TEST_CHAT_MESSAGE_RESPONSE1 = new ChatMessageResponse("황주환","안녕하세요", LocalDateTime.of(2021,7,23,10,0));
    public static final ChatMessageResponse TEST_CHAT_MESSAGE_RESPONSE2 = new ChatMessageResponse("황주환","안녕하세요", LocalDateTime.of(2021,7,23,10,1));
    public static final ChatMessageResponse TEST_CHAT_MESSAGE_RESPONSE3 = new ChatMessageResponse("황주환","안녕하세요", LocalDateTime.of(2021,7,23,10,2));

    public static final StudyDeleteMessage TEST_STUDY_DELETE_MESSAGE = new StudyDeleteMessage(1L);

    public static ChatRoom createTestChatRoom(){
        return new ChatRoom(1L,"공지사항",1L);
    }
}
