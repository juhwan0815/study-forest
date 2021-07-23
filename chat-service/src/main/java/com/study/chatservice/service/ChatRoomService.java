package com.study.chatservice.service;

import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;

public interface ChatRoomService {

    ChatRoomResponse create(Long studyId, ChatRoomCreateRequest request);
}
