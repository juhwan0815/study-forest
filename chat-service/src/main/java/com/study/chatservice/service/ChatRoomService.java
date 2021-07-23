package com.study.chatservice.service;

import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.model.chatroom.ChatRoomUpdateRequest;

public interface ChatRoomService {

    ChatRoomResponse create(Long studyId, ChatRoomCreateRequest request);

    ChatRoomResponse update(Long chatRoomId, ChatRoomUpdateRequest request);

}
