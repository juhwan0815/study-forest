package com.study.notificationservice.fcm;

public interface FcmMessageSender {

    void send(String tokenId, String title, String content);
}
