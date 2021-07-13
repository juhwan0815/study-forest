package com.study.studyservice.model.waituser;

import com.study.studyservice.domain.WaitUser;
import com.study.studyservice.model.user.UserImage;
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

    private UserImage image; // 회원 이미지

    private String gender; // 성별

    private String ageRange; // 나이대

    public static WaitUserResponse from(WaitUser waitUser,UserResponse userResponse){
        WaitUserResponse waitUserResponse = new WaitUserResponse();
        waitUserResponse.id = waitUser.getId();
        waitUserResponse.userId = waitUser.getUserId();
        waitUserResponse.nickName = userResponse.getNickName();
        waitUserResponse.image = userResponse.getImage();
        waitUserResponse.gender = userResponse.getGender();
        waitUserResponse.ageRange = userResponse.getAgeRange();
        return waitUserResponse;
    }
}
