package com.study.chatservice.model.chatMessage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.chatservice.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.NegativeOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

    private Long userId;

    private String sender;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage chatMessage){
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.userId = chatMessage.getUserId();
        chatMessageResponse.sender = chatMessage.getSender();
        chatMessageResponse.message = chatMessage.getMessage();
        chatMessageResponse.createdAt = LocalDateTime.now();
        return chatMessageResponse;
    }
}
