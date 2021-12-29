package com.study.fcm;

public interface FcmMessageSender {

    void send(String fcmToken, String title, String content);
}
