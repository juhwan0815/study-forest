package com.study.notificationservice.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class FcmMessageSenderImpl implements FcmMessageSender {

    @Value("${fcm.tempFilePath}")
    private String FIREBASE_CONFIG_PATH;

    @PostConstruct
    public void initialize() {
        try {
            InputStream fcmOptionsInputStream = FIREBASE_CONFIG_PATH.startsWith("/") ? new FileSystemResource(FIREBASE_CONFIG_PATH).getInputStream()
                    : new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(fcmOptionsInputStream)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void send(String tokenId, String title, String content) {
        Message message = Message.builder()
                .setToken(tokenId)
                .putData("title", title)
                .putData("message", content)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent message: " + response);
        } catch (ExecutionException e) {
            log.error("푸쉬알림 전송 실패");
        } catch (InterruptedException e) {
            log.error("푸쉬알림 전송 실패");
        }
    }

}
