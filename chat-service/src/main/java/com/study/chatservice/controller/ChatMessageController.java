package com.study.chatservice.controller;

import com.study.chatservice.model.chatMessage.ChatMessageResponse;
import com.study.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/chatRooms/{chatRoomId}/chatMessages")
    public ResponseEntity<Page<ChatMessageResponse>> findByChatRoomId(
            @PathVariable Long chatRoomId,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return ResponseEntity.ok(chatMessageService.findByChatRoomId(chatRoomId, pageable));
    }
}
