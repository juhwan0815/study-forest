package com.study.notificationservice.domain;

import com.study.notificationservice.exception.NotificationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long userId;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    public static Notification createNotification(Long userId,String title,String content){
        Notification notification = new Notification();
        notification.userId = userId;
        notification.title = title;
        notification.content = content;
        notification.status = Status.NOTREAD;
        return notification;
    }



}
