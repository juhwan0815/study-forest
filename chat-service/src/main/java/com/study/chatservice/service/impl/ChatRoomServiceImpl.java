package com.study.chatservice.service.impl;

import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.exception.ChatRoomException;
import com.study.chatservice.kafka.message.StudyDeleteMessage;
import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.model.chatroom.ChatRoomUpdateRequest;
import com.study.chatservice.repository.ChatMessageRepository;
import com.study.chatservice.repository.ChatRoomRepository;
import com.study.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public ChatRoomResponse create(Long studyId, ChatRoomCreateRequest request) {

        validateDuplicatedChatRoom(studyId, request.getName());

        ChatRoom chatRoom = ChatRoom.createChatRoom(request.getName(), studyId);
        chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(chatRoom);
    }

    @Override
    @Transactional
    public ChatRoomResponse update(Long chatRoomId, ChatRoomUpdateRequest request) {
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomException("존재하지 않는 채팅방입니다."));

        validateDuplicatedChatRoom(findChatRoom.getStudyId(),request.getName());

        findChatRoom.changeName(request.getName());

        return ChatRoomResponse.from(findChatRoom);
    }

    @Override
    @Transactional
    public void delete(Long chatRoomId) {
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomException("존재하지 않는 채팅방입니다."));

        chatRoomRepository.delete(findChatRoom);
        chatMessageRepository.deleteByChatRoomId(findChatRoom.getId());
    }

    @Override
    public List<ChatRoomResponse> findByStudyId(Long studyId) {
        return chatRoomRepository.findByStudyId(studyId).stream()
                .map(chatRoom -> ChatRoomResponse.from(chatRoom))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChatRoomsAndChatMessages(StudyDeleteMessage studyDeleteMessage) {

        List<ChatRoom> findChatRooms = chatRoomRepository.findByStudyId(studyDeleteMessage.getStudyId());

        findChatRooms.stream().forEach(chatRoom -> {
            chatRoomRepository.delete(chatRoom);
            chatMessageRepository.deleteByChatRoomId(chatRoom.getId());
        });
    }

    private void validateDuplicatedChatRoom(Long studyId, String name) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByNameAndAndStudyId(name, studyId);
        if (optionalChatRoom.isPresent()){
            throw new ChatRoomException("이미 같은 이름의 채팅방이 존재합니다.");
        }
    }
}
