package com.study.studyservice.model.waituser;

import com.study.studyservice.domain.WaitUser;
import com.study.studyservice.model.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class WaitUserResponse {

    private Long id;

    private Long userId;

    private String nickName;

    public static WaitUserResponse from(WaitUser waitUser,UserResponse userResponse){
        WaitUserResponse waitUserResponse = new WaitUserResponse();
        waitUserResponse.id = waitUser.getId();
        waitUserResponse.userId = waitUser.getUserId();
        waitUserResponse.nickName = userResponse.getNickName();
        return waitUserResponse;
    }
}
