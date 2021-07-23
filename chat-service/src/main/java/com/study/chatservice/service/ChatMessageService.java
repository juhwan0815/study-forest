package com.study.chatservice.service;

import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {

    Page<ChatMessageResponse> findByChatRoomId(Long chatRoomId, Pageable pageable);
}
