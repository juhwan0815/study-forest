package com.study.notification;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long userId;

    private String title;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Notification createNotification(Long userId, String title, String content) {
        Notification notification = new Notification();
        notification.userId = userId;
        notification.title = title;
        notification.content = content;
        return notification;
    }
}
