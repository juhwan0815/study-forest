package com.study.controller;

import com.study.dto.MessageRequest;
import com.study.dto.MessageResponse;
import com.study.service.MessageService;
import com.study.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final JwtUtils jwtUtils;
    private final SimpMessageSendingOperations messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chatRoom/message")
    public void sendMessage(MessageRequest request, @Header(HttpHeaders.AUTHORIZATION) String bearerToken) {
        Long userId = jwtUtils.getUserId(bearerToken.substring(7));
        String sender = jwtUtils.getNickName(bearerToken.substring(7));

        MessageResponse messageResponse = messageService.sendMessage(userId, sender, request);
        messagingTemplate.convertAndSend("/sub/chatRoom/" + request.getRoomId(), messageResponse);
    }
}
