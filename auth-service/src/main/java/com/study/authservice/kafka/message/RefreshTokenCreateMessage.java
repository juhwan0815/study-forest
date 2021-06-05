package com.study.authservice.kafka.message;

import lombok.Data;

@Data
public class RefreshTokenCreateMessage {

    private Long id;

    private String refreshToken;

    public static RefreshTokenCreateMessage from(Long id,String refreshToken){
        RefreshTokenCreateMessage refreshTokenCreateMessage = new RefreshTokenCreateMessage();
        refreshTokenCreateMessage.id = id;
        refreshTokenCreateMessage.refreshToken = refreshToken;
        return refreshTokenCreateMessage;
    }
}
