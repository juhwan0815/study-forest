package com.study.userservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("회원 Refresh 토큰 수정")
    void updateRefreshToken(){

        User user = User.createUser(1L,"황주환","image", "image", UserRole.USER);
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODk2NDgsImV4cCI6MTYyMzQ5NDQ0OH0.nqpEwiL-WCbnxGoUoRv3kPN_wyoLnOat0TZaOXxBWk0";

        user.updateRefreshToken(refreshToken);

        assertThat(user.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("회원 로그아웃")
    void logout(){
        User user = User.createUser(1L,"황주환","image", "image", UserRole.USER);
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODk2NDgsImV4cCI6MTYyMzQ5NDQ0OH0.nqpEwiL-WCbnxGoUoRv3kPN_wyoLnOat0TZaOXxBWk0";
        user.updateRefreshToken(refreshToken);

        user.logout();

        assertThat(user.getRefreshToken()).isNull();
    }


}