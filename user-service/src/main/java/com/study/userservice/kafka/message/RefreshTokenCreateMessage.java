package com.study.userservice.kafka.message;

import lombok.Data;

@Data
public class RefreshTokenCreateMessage {

    private Long id;

    private String refreshToken;
}
