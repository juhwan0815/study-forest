package com.study.studyservice.model.studyuser;

import com.study.studyservice.domain.Role;
import com.study.studyservice.domain.StudyUser;
import com.study.studyservice.model.user.UserImage;
import com.study.studyservice.model.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyUserResponse {

    private Long id;

    private Long userId;

    private String nickName;

    private UserImage image;

    private String gender;

    private String ageRange;

    private Role role;

    public static StudyUserResponse from(StudyUser studyUser, UserResponse userResponse){
        StudyUserResponse studyUserResponse = new StudyUserResponse();
        studyUserResponse.id = studyUser.getId();
        studyUserResponse.userId = studyUser.getUserId();
        studyUserResponse.nickName = userResponse.getNickName();
        studyUserResponse.image = userResponse.getImage();
        studyUserResponse.gender = userResponse.getGender();
        studyUserResponse.ageRange = userResponse.getAgeRange();
        studyUserResponse.role = studyUser.getRole();
        return studyUserResponse;
    }
}
