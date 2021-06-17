package com.study.userservice.kafka.message;

import com.study.userservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateProfileMessage {

    private Long userId;

    private String nickName;

    public static UserUpdateProfileMessage from(User user){
        UserUpdateProfileMessage userUpdateProfileMessage = new UserUpdateProfileMessage();
        userUpdateProfileMessage.userId = user.getId();
        userUpdateProfileMessage.nickName = user.getNickName();
        return userUpdateProfileMessage;
    }

}
