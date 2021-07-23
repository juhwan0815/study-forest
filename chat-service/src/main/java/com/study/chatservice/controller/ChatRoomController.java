package com.study.chatservice.controller;

import com.study.chatservice.config.LoginUser;
import com.study.chatservice.model.chatroom.ChatRoomCreateRequest;
import com.study.chatservice.model.chatroom.ChatRoomResponse;
import com.study.chatservice.model.chatroom.ChatRoomUpdateRequest;
import com.study.chatservice.service.ChatRoomService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/studies/{studyId}/chatRooms")
    public ResponseEntity<ChatRoomResponse> create(@PathVariable Long studyId,
                                                   @RequestBody @Valid ChatRoomCreateRequest request){
        return ResponseEntity.ok(chatRoomService.create(studyId,request));
    }

    // 채팅방 수정
    @PatchMapping("/chatRooms/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> update(@PathVariable Long chatRoomId,
                                                   @RequestBody @Valid ChatRoomUpdateRequest request){
        return ResponseEntity.ok(chatRoomService.update(chatRoomId,request));
    }



    // 채팅방 삭제

    // 채팅방 조회

}
