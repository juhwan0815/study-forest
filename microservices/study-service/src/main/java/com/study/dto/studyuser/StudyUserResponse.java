package com.study.dto.studyuser;

import com.study.client.UserResponse;
import com.study.domain.StudyRole;
import com.study.domain.StudyUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyUserResponse {

    private Long userId; // 회원 ID

    private StudyRole studyRole; // 유저 권한

    private String nickName; // 닉네임

    private String ageRange; // 나이대

    private String gender; // 성별

    private String imageUrl; // 이미지

    private String fcmToken; // FCM 토큰

    public static StudyUserResponse from(StudyUser studyUser, List<UserResponse> users) {
        UserResponse findUser = users.stream()
                .filter(user -> user.getUserId().equals(studyUser.getUserId()))
                .findFirst().get();
        StudyUserResponse studyUserResponse = new StudyUserResponse();
        studyUserResponse.userId = studyUser.getUserId();
        studyUserResponse.studyRole = studyUser.getStudyRole();
        studyUserResponse.nickName = findUser.getNickName();
        studyUserResponse.ageRange = findUser.getAgeRange();
        studyUserResponse.gender = findUser.getGender();
        studyUserResponse.imageUrl = findUser.getImageUrl();
        studyUserResponse.fcmToken = findUser.getFcmToken();
        return studyUserResponse;
    }
}
