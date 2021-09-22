package com.study.chatservice.service.impl;

import com.study.chatservice.domain.ChatMessage;
import com.study.chatservice.domain.ChatRoom;
import com.study.chatservice.exception.ChatRoomException;
import com.study.chatservice.kafka.message.ChatCreateMessage;
import com.study.chatservice.kafka.sender.ChatCreateMessageSender;
import com.study.chatservice.model.chatMessage.ChatMessageRequest;
import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import com.study.chatservice.repository.ChatMessageRepository;
import com.study.chatservice.repository.ChatRoomInfoRepository;
import com.study.chatservice.repository.ChatRoomRepository;
import com.study.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomInfoRepository chatRoomInfoRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatCreateMessageSender chatCreateMessageSender;

    @Override
    public Page<ChatMessageResponse> findByChatRoomId(Long chatRoomId, Pageable pageable,String lastMessageDate) {
        return chatMessageRepository
                .findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(pageable,chatRoomId, LocalDateTime.parse(lastMessageDate))
                .map(chatMessage -> ChatMessageResponse.from(chatMessage));
    }

    @Override
    @Transactional
    public void sendChatMessage(ChatMessageRequest request) {
        ChatMessage chatMessage = ChatMessage
                .createMessage(request.getUserId(),request.getSender(), request.getMessage(), request.getRoomId());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);

        chatMessageRepository.save(chatMessage);

        ChatRoom findChatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ChatRoomException("존재하지 않는 채팅방입니다."));

        List<Long> chatRoomUserIds = chatRoomInfoRepository.getChatRoomUser(String.valueOf(request.getRoomId()));

        chatCreateMessageSender.send(ChatCreateMessage.createChatCreateMessage(findChatRoom.getStudyId(),
                findChatRoom.getName(), chatMessage.getSender(), chatMessage.getMessage(), chatRoomUserIds));
    }
}
