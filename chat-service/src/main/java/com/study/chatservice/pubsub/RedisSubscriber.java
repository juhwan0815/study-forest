package com.study.chatservice.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.chatservice.domain.ChatMessage;
import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * Redis 구독 서비스 구현
 * Redis에 메세지 발행이 될 때까지 대기하였다가 메세지가 발행되면 해당 메세지를 읽어 처리하는 리스너
 * 1. Redis에 메세지가 발행되면 해당 메세지를 ChatMessage로 변환
 * 2. messaging Template를 이용하여 채팅방의 모든 웹소켓 클라이언트들에게 메세지를 전달
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메세지가 발행되면 대기하고 있던 Redis Subscriber가 해당 메세지를 받아 처리
     */
    public void sendMessage(String publishMessage){
        try{
            // ChatMessage 객체로 매핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // 채팅방을 구독한 클라이언트에게 메세지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/"+chatMessage.getChatRoomId(),ChatMessageResponse.from(chatMessage));
        }catch (Exception e){
            log.error("Exception {}",e);
        }
    }

}
