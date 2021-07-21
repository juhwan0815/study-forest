package com.study.authservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id; // 회원 Id

    private String role; // 회원 권한

    private String nickName; // 회원 이름

}
