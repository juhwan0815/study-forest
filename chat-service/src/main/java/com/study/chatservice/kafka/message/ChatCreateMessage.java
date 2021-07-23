package com.study.chatservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreateMessage {

    private Long studyId;

    private String chatRoomName;

    private String nickName;

    private String content;

    private List<Long> userIdList;

    public static ChatCreateMessage createChatCreateMessage(Long studyId,String chatRoomName,String nickName,
                                                            String content,List<Long> userIdList){
        ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
        chatCreateMessage.studyId = studyId;
        chatCreateMessage.chatRoomName = chatRoomName;
        chatCreateMessage.nickName = nickName;
        chatCreateMessage.content = content;
        chatCreateMessage.userIdList = userIdList;
        return chatCreateMessage;
    }
}
