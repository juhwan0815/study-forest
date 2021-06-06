package com.study.authservice.kafka.message;

import lombok.Data;

@Data
public class LogoutMessage {

    private Long userId;

    public static LogoutMessage from(Long userId){
        LogoutMessage logoutMessage = new LogoutMessage();
        logoutMessage.userId = userId;
        return logoutMessage;
    }
}

