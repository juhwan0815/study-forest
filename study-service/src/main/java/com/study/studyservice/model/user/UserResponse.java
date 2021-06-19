package com.study.studyservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id; // 회원 Id

    private String nickName; // 닉네임
}
