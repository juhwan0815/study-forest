package com.study.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {

    private Long userId;

    private String sender;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static MessageResponse from(Message message){
        return new MessageResponse(message.getUserId(), message.getSender(), message.getContent(), LocalDateTime.now());
    }

}
