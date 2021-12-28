package com.study.service;

import com.study.domain.Message;
import com.study.dto.MessageRequest;
import com.study.dto.MessageResponse;
import com.study.kafka.sender.MessageCreateSender;
import com.study.kakfa.MessageCreateMessage;
import com.study.repository.ChatRoomRepository;
import com.study.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MessageCreateSender messageCreateSender;

    @Override
    public Slice<MessageResponse> findByRoomId(Long roomId, Pageable pageable, String lastMessageDate) {
        Slice<Message> messages = messageRepository.findByRoomIdAndCreatedAtBeforeOrOrderByIdDesc(pageable, roomId, LocalDateTime.parse(lastMessageDate));
        return messages.map(message -> MessageResponse.from(message));
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long userId, String sender, MessageRequest request) {
        Message message = Message.createMessage(userId, sender, request.getContent(), request.getRoomId());
        messageRepository.save(message);

        List<Long> userIds = chatRoomRepository.getChatRoomUsers(request.getRoomId(), userId);
        messageCreateSender.send(MessageCreateMessage.from(request.getRoomId(), sender, request.getContent(), userIds));
        return MessageResponse.from(message);
    }
}
