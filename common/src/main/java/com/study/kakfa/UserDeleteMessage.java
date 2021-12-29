package com.study.kakfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteMessage {

    private Long userId;

    public static UserDeleteMessage from(Long userId) {
        return new UserDeleteMessage(userId);
    }
}
