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

    @Test
    @DisplayName("회원 썸네일 이미지 변경")
    void changeThumbnailImage(){
        User user = User.createUser(1L,"황주환","image", "image", UserRole.USER);
        String thumbnailImageURL = "썸네일이미지URL";
        String thumbnailImageStoreName = "썸네일이미지";

        user.changeThumbnailImage(thumbnailImageURL, thumbnailImageStoreName);

        assertThat(user.getThumbnailImage()).isEqualTo(thumbnailImageURL);
        assertThat(user.getThumbnailImageStoreName()).isEqualTo(thumbnailImageStoreName);
    }

    @Test
    @DisplayName("회원 이미지 변경")
    void changeImage(){
        User user = User.createUser(1L,"황주환","image", "image", UserRole.USER);
        String profileImageURL = "이미지URL";
        String profileImageStoreName = "이미지";

        user.changeImage(profileImageURL, profileImageStoreName);

        assertThat(user.getProfileImage()).isEqualTo(profileImageURL);
        assertThat(user.getProfileImageStoreName()).isEqualTo(profileImageStoreName);
    }

    @Test
    @DisplayName("회원 이름 변경")
    void changeNickName(){
        User user = User.createUser(1L,"황주환","image", "image", UserRole.USER);
        String changeNickName = "황철원";

        user.changeNickName(changeNickName);

        assertThat(user.getNickName()).isEqualTo(changeNickName);
    }


}