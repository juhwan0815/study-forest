package com.study.userservice.kafka.message;

import lombok.Data;

@Data
public class LogoutMessage {

    private Long userId;
}
