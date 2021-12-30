package com.study;

import com.study.client.ChatRoomResponse;
import com.study.client.StudyResponse;
import com.study.client.UserResponse;
import com.study.domain.Notification;
import com.study.dto.NotificationResponse;

import java.time.LocalDateTime;

public class NotificationFixture {

    public static final Notification TEST_NOTIFICATION = Notification.createNotification(1L, "알림 제목", "알림 내용");

    public static final NotificationResponse TEST_NOTIFICATION_RESPONSE = new NotificationResponse(1L, 1L,"알림 제목", "알림 내용", LocalDateTime.now());

    public static final UserResponse TEST_USER_RESPONSE = new UserResponse(1L, "황주환","10~19","male","이미지 URL","fcmToken");

    public static final StudyResponse TEST_STUDY_RESPONSE = new StudyResponse(1L, "테스트 스터디");

    public static final ChatRoomResponse TEST_CHATROOM_RESPONSE = new ChatRoomResponse(1L, "테스트 채팅방");
}
