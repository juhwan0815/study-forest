package com.study.chatservice.model.chatroom;

import com.study.chatservice.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {

    private Long id;

    private String name;

    private Long studyId;

    public static ChatRoomResponse from(ChatRoom chatRoom){
        ChatRoomResponse chatRoomResponse = new ChatRoomResponse();
        chatRoomResponse.id = chatRoom.getId();
        chatRoomResponse.name = chatRoom.getName();
        chatRoomResponse.studyId = chatRoom.getStudyId();
        return chatRoomResponse;
    }
}
