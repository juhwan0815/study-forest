package com.study.kakfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDeleteMessage {

    private Long chatRoomId;

    public static ChatRoomDeleteMessage from(Long chatRoomId){
        return new ChatRoomDeleteMessage(chatRoomId);
    }
}
