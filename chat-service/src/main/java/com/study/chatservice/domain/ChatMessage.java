package com.study.chatservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    private String sender;

    private String message;

    private Long chatRoomId;

    @CreatedDate
    private LocalDateTime createAt;

    public static ChatMessage createMessage(String sender,String message,Long chatRoomId){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.sender = sender;
        chatMessage.message = message;
        chatMessage.chatRoomId = chatRoomId;
        return chatMessage;
    }
}
