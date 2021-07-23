package com.study.chatservice.service.impl;

import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.exception.ChatRoomException;
import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.repository.ChatRoomRepository;
import com.study.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public ChatRoomResponse create(Long studyId, ChatRoomCreateRequest request) {

        validateDuplicatedChatRoom(studyId, request.getName());

        ChatRoom chatRoom = ChatRoom.createChatRoom(request.getName(), studyId);
        chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(chatRoom);
    }

    private void validateDuplicatedChatRoom(Long studyId, String name) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByNameAndAndStudyId(name, studyId);
        if (optionalChatRoom.isPresent()){
            throw new ChatRoomException("이미 같은 이름의 채팅방이 존재합니다.");
        }
    }
}
