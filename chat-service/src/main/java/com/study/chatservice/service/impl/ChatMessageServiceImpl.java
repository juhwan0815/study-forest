package com.study.chatservice.service.impl;

import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import com.study.chatservice.repository.ChatMessageRepository;
import com.study.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private ChatMessageRepository chatMessageRepository;

    @Override
    public Page<ChatMessageResponse> findByChatRoomId(Long chatRoomId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(pageable,chatRoomId)
                .map(chatMessage -> ChatMessageResponse.from(chatMessage));
    }
}
