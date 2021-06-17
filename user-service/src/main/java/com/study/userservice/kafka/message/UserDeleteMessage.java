package com.study.userservice.kafka.message;

import com.study.userservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteMessage {

    private Long userId;

    public static UserDeleteMessage from(User user){
        UserDeleteMessage userDeleteMessage = new UserDeleteMessage();
        userDeleteMessage.userId = user.getId();
        return userDeleteMessage;
    }

}
