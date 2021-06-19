package com.study.studyservice.model.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserResponse {

    private Long id; // 회원 Id

    private String nickName; // 닉네임
}
