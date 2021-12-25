package com.study.dto.chatroom;

import com.study.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {

    private Long chatRoomId;

    private String name;

    public static ChatRoomResponse from(ChatRoom chatRoom){
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getName());
    }
}
