package com.study.fcm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FcmMessageSenderTest {

    @Autowired
    private FcmMessageSender fcmMessageSender;

    @Test
    @DisplayName("FCM 메세지를 전송한다.")
    void send() {
        // when
        fcmMessageSender.send("fgwN4QJzhKTbA5aMqa2DLY:APA91bGNZ-ihCr-JZZBAHuaxhUjhzII4F2GBCs5DavyU4ocW5NZcOeVyaFTC_Od9468V7SdM3m3rHbIubsbCrluYWeY7sAsygFSexHdSEHvRWEubW6iIxsVD174Gcyp83vhld6gPG00T","테스트 제목", "테스트 내용");
    }

    @Test
    @DisplayName("FCM 메세지를 전송을 실패한다.")
    void sendFail() {
        // when
        fcmMessageSender.send("fcmToken","테스트 제목", "테스트 내용");
    }
}