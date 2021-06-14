package com.study.userservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useRepresentation;

class UserTest {

    @Test
    @DisplayName("회원 Refresh 토큰 수정")
    void updateRefreshToken(){

        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODk2NDgsImV4cCI6MTYyMzQ5NDQ0OH0.nqpEwiL-WCbnxGoUoRv3kPN_wyoLnOat0TZaOXxBWk0";

        user.updateRefreshToken(refreshToken);

        assertThat(user.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("회원 로그아웃")
    void logout(){
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiUk9MRSI6IlVTRVIiLCJpYXQiOjE2MjI4ODk2NDgsImV4cCI6MTYyMzQ5NDQ0OH0.nqpEwiL-WCbnxGoUoRv3kPN_wyoLnOat0TZaOXxBWk0";
        user.updateRefreshToken(refreshToken);

        user.logout();

        assertThat(user.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("회원 이미지 삭제")
    void deleteImage(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);

        // when
        user.deleteImage();

        // then
        assertThat(user.getProfileImage()).isNull();
        assertThat(user.getThumbnailImage()).isNull();
        assertThat(user.getImageStoreName()).isNull();;
    }

    @Test
    @DisplayName("회원 이미지 수정")
    void changeImage(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);

        // when
        user.changeImage("changedImage","changeThumbnailImage","imageStoreName");

        // then
        assertThat(user.getProfileImage()).isEqualTo("changedImage");
        assertThat(user.getThumbnailImage()).isEqualTo("changeThumbnailImage");
        assertThat(user.getImageStoreName()).isEqualTo("imageStoreName");
    }

    @Test
    @DisplayName("회원 닉네임 수정")
    void changeNickName(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);

        // when
        user.changeNickName("황철원");

        // then
        assertThat(user.getNickName()).isEqualTo("황철원");
    }

    @Test
    @DisplayName("회원 스터디 참가 신청 내역 추가")
    void addStudyJoin(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);

        // when
        user.addStudyJoin(1L);

        // then
        assertThat(user.getStudyJoins().size()).isEqualTo(1);
        assertThat(user.getStudyJoins().get(0).getStudyId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원 스터디 참가 신청 내역 취소")
    void failStudyJoin(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        user.getStudyJoins().add(StudyJoin.createTestStudyJoin(1L,1L,user));
        user.getStudyJoins().add(StudyJoin.createTestStudyJoin(2L,1L,user));

        // when
        user.failStudyJoin(1L);

        // then
        assertThat(user.getStudyJoins().get(1).getStatus()).isEqualTo(StudyJoinStatus.FAIL);
    }

    @Test
    @DisplayName("회원 스터디 참가 신청 내역 성공")
    void successStudyJoin(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        user.getStudyJoins().add(StudyJoin.createTestStudyJoin(1L,1L,user));
        user.getStudyJoins().add(StudyJoin.createTestStudyJoin(2L,1L,user));

        // when
        user.successStudyJoin(1L);

        // then
        assertThat(user.getStudyJoins().get(1).getStatus()).isEqualTo(StudyJoinStatus.SUCCESS);
    }

}