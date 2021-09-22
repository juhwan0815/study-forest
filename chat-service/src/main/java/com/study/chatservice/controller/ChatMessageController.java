package com.study.chatservice.controller;

import com.study.chatservice.config.security.JwtTokenProvider;
import com.study.chatservice.domain.ChatMessage;
import com.study.chatservice.model.chatMessage.ChatMessageRequest;
import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import com.study.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/chatRooms/{chatRoomId}/chatMessages")
    public ResponseEntity<Page<ChatMessageResponse>> findByChatRoomId(
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            @RequestParam String lastMessageDate) {
        return ResponseEntity.ok(chatMessageService.findByChatRoomId(chatRoomId, pageable,lastMessageDate));
    }

    /**
     * @MessageMapping을 통해 웹 소켓으로 들어오는 메세지 발행을 처리
     *  웹 소켓 "/pub/chat/message" 로 들어오는 메세지 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequest message, @Header("token") String token){
        Long userId = jwtTokenProvider.getUserIdFromJwt(token);
        String nickname = jwtTokenProvider.getNicknameFromJwt(token);

        // 로그인 화면 정보로 대화명설정
        message.setUserId(userId);
        message.setSender(nickname);

        // 웹 소켓에 발행된 메세지를 redis로 발행한다. publish
        chatMessageService.sendChatMessage(message);
    }
}
