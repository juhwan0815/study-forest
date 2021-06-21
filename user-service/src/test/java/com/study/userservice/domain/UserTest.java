package com.study.userservice.domain;

import com.study.userservice.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    @DisplayName("회원의 이미지를 수정한다.")
    void changeImage(){
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        Image image = Image.createImage("썸네일 이미지", "이미지", "이미지 저장 이름");

        user.changeImage(image);

        assertThat(user.getImage()).isNotNull();
        assertThat(user.getImage().getProfileImage()).isEqualTo("이미지");
        assertThat(user.getImage().getThumbnailImage()).isEqualTo("썸네일 이미지");
        assertThat(user.getImage().getImageStoreName()).isEqualTo("이미지 저장 이름");
    }

    @Test
    @DisplayName("회원의 닉네임을 수정한다.")
    void changeNickName(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.changeNickName("황철원");

        // then
        assertThat(user.getNickName()).isEqualTo("황철원");
    }

    @Test
    @DisplayName("회원의 지역을 변경한다.")
    void changeLocation(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.changeLocation(1L);

        // then
        assertThat(user.getLocationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예외 테스트 : 이미 관심태그에 존재하는 태그를 추가할 경우 예외가 발생한다.")
    void checkExistTag(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);
        user.addInterestTag(1L);

        // when
        assertThrows(UserException.class,()-> user.checkExistTag(1L));
    }

    @Test
    @DisplayName("관심 태그에 태그를 추가한다.")
    void addInterestTag(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.addInterestTag(1L);

        // then
        assertThat(user.getInterestTags().size()).isEqualTo(1);
        assertThat(user.getInterestTags().get(0).getTagId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("관심 태그를 삭제한다.")
    void deleteInterestTag(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);
        user.addInterestTag(1L);

        // when
        user.deleteInterestTag(1L);

        // then
        assertThat(user.getInterestTags().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("예외 테스트 : 관심 태그 목록에 존재하지 않는 태그를 삭제할 경우 예외가 발생한다.")
    void deleteInterestTagWhenNotExist(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        assertThrows(UserException.class,()->user.deleteInterestTag(1L));
    }

    @Test
    @DisplayName("스터디 참가 신청 이력을 추가한다.")
    void addStudyApply(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);

        // when
        user.addStudyApply(1L);

        // then
        assertThat(user.getStudyApplies().size()).isEqualTo(1);
        assertThat(user.getStudyApplies().get(0).getStudyId()).isEqualTo(1L);
        assertThat(user.getStudyApplies().get(0).getStatus()).isEqualTo(StudyApplyStatus.WAIT);
        assertThat(user.getNumberOfStudyApply()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 참가 신청 이력을 실패로 변경한다.")
    void failStudyApply(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);
        user.addStudyApply(1L);

        // when
        user.failStudyApply(1L);

        // then
        assertThat(user.getStudyApplies().size()).isEqualTo(1);
        assertThat(user.getStudyApplies().get(0).getStatus()).isEqualTo(StudyApplyStatus.FAIL);
    }

    @Test
    @DisplayName("스터디 참가 신청 이력을 성공으로 변경한다.")
    void SuccessStudyApply(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);
        user.addStudyApply(1L);

        // when
        user.successStudyApply(1L);

        // then
        assertThat(user.getStudyApplies().size()).isEqualTo(1);
        assertThat(user.getStudyApplies().get(0).getStatus()).isEqualTo(StudyApplyStatus.SUCCESS);
    }

}