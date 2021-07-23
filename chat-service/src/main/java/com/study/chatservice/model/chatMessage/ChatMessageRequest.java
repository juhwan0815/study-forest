package com.study.chatservice.model.chatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {

    private Long userId; // 메세지 보낸 사람 ID

    private Long roomId; // 방번호

    private String sender; // 메세지 보낸 사람

    private String message; // 메세지
}
