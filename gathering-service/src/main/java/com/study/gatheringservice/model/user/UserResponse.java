package com.study.gatheringservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id; // 회원 Id

    private String nickName; // 닉네임

    private UserImage image; // 회원 이미지

    private String gender; // 성별

    private String ageRange; // 나이대

    private Integer searchDistance; // 오프라인 검색 거리

    private Long locationId; // 회원 지역
}
