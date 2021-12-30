package com.study.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long notificationId;

    private Long userId;

    private String title;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.notificationId = notification.getId();
        notificationResponse.title = notification.getTitle();
        notificationResponse.userId = notification.getUserId();
        notificationResponse.content = notification.getContent();
        notificationResponse.createdAt = notification.getCreatedAt();
        return notificationResponse;
    }
}
