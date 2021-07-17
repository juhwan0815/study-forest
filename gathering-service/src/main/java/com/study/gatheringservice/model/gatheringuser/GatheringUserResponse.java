package com.study.gatheringservice.model.gatheringuser;

import com.study.gatheringservice.domain.GatheringUser;
import com.study.gatheringservice.model.user.UserImage;
import com.study.gatheringservice.model.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringUserResponse {

    private Long id;

    private Long userId;

    private String nickName;

    private UserImage image;

    private String gender;

    private String ageRange;

    private Boolean register;

    public static GatheringUserResponse from(GatheringUser gatheringUser, UserResponse user){
        GatheringUserResponse gatheringUserResponse = new GatheringUserResponse();
        gatheringUserResponse.id = gatheringUser.getId();
        gatheringUserResponse.userId = gatheringUser.getUserId();
        gatheringUserResponse.nickName = user.getNickName();
        gatheringUserResponse.image = user.getImage();
        gatheringUserResponse.gender = user.getGender();
        gatheringUserResponse.ageRange = user.getAgeRange();
        gatheringUserResponse.register = gatheringUser.getRegister();
        return gatheringUserResponse;
    }
}
