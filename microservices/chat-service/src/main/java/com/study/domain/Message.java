package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    private Long userId;

    private String sender;

    private String content;

    private Long roomId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static Message createMessage(Long userId, String sender, String content, Long roomId) {
        Message message = new Message();
        message.userId = userId;
        message.sender = sender;
        message.content = content;
        message.roomId = roomId;
        return message;
    }
}
