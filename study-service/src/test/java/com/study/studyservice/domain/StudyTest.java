package com.study.studyservice.domain;

import com.study.studyservice.exception.StudyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.study.studyservice.fixture.TagFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudyTest {

    @Test
    @DisplayName("스터디 참가자를 추가한다.")
    void addStudyUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        // when
        study.addStudyUser(1L,Role.USER);

        // then
        assertThat(study.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(study.getStudyUsers().size()).isEqualTo(1);
        assertThat(study.getStudyUsers().get(0).getUserId()).isEqualTo(1L);
        assertThat(study.getStudyUsers().get(0).getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("스터디 참가자수와 스터디의 정원이 같으면 스터디의 상태가 CLOSE 로 변경된다.")
    void addStudyUserStatusClose(){
        // given
        Study study = Study.createStudy("테스트 스터디", 2, "테스트 스터디입니다.",
                true, true, null);
        study.addStudyUser(1L,Role.USER);

        // when
        study.addStudyUser(2L,Role.USER);

        // then
        assertThat(study.getStatus()).isEqualTo(StudyStatus.CLOSE);
    }

    @Test
    @DisplayName("예외테스트 : 스터디가 마감되었을 경우 스터디 참가 인원을 추가하면 예외가 발생한다")
    void addStudyUsersWhenStatusClose(){
        // given
        Study study = Study.createStudy("테스트 스터디", 2, "테스트 스터디입니다.",
                true, true, null);
        study.addStudyUser(1L,Role.USER);
        study.addStudyUser(2L,Role.USER);

        // when
        assertThrows(StudyException.class,()->study.addStudyUser(3L,Role.USER));
    }

    @Test
    @DisplayName("스터디의 이미지를 변경한다.")
    void changeImage(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        Image image = Image.createImage("테스트 이미지", "테스트 썸네일 이미지", "테스트 이미지 저장 이름");

        // when
        study.changeImage(image);

        // then
        assertThat(study.getImage()).isEqualTo(image);
        assertThat(study.getImage().getStudyImage()).isEqualTo("테스트 이미지");
        assertThat(study.getImage().getThumbnailImage()).isEqualTo("테스트 썸네일 이미지");
        assertThat(study.getImage().getImageStoreName()).isEqualTo("테스트 이미지 저장 이름");
    }

    @Test
    @DisplayName("스터디의 지역정보를 변경한다.")
    void changeLocation(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        // when
        study.changeLocation(1L);

        // then
        assertThat(study.getLocationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("스터디의 태그를 추가한다.")
    void addStudyTags(){
       // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        List<Tag> tags = new ArrayList<>();
        tags.add(TEST_TAG1);
        tags.add(TEST_TAG2);
        tags.add(TEST_TAG3);

        // when
        study.addStudyTags(tags);

        // then
        assertThat(study.getStudyTags().size()).isEqualTo(3);
        assertThat(study.getStudyTags().get(0).getTag()).isEqualTo(TEST_TAG1);
        assertThat(study.getStudyTags().get(1).getTag()).isEqualTo(TEST_TAG2);
        assertThat(study.getStudyTags().get(2).getTag()).isEqualTo(TEST_TAG3);
    }

    @Test
    @DisplayName("예외테스트 : 스터디의 현재 인원이 변경할 인원보다 많을 경우 예외가 발생한다.")
    void checkNumberOfStudyUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        study.addStudyUser(1L,Role.USER);

        // when
        assertThrows(StudyException.class,()->study.checkNumberOfStudyUser(0));
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    void update(){
        // given
        Category category = Category.createCategory("백엔드", null);
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, category);

        Category changeCategory = Category.createCategory("프론트엔드", null);

        // when
        study.update("스프링 스터디",10,"스프링 스터디입니다.",
                false,false,true,changeCategory);

        assertThat(study.getName()).isEqualTo("스프링 스터디");
        assertThat(study.getNumberOfPeople()).isEqualTo(10);
        assertThat(study.getContent()).isEqualTo("스프링 스터디입니다.");
        assertThat(study.isOnline()).isEqualTo(false);
        assertThat(study.isOffline()).isEqualTo(false);
        assertThat(study.getStatus()).isEqualTo(StudyStatus.CLOSE);
        assertThat(study.getCategory()).isEqualTo(changeCategory);
    }

    @Test
    @DisplayName("스터디의 태그를 수정한다.")
    void updateStudyTags(){
        // givene
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        List<Tag> tags = new ArrayList<>();
        tags.add(TEST_TAG1);
        tags.add(TEST_TAG2);
        tags.add(TEST_TAG3);
        study.addStudyTags(tags);

        List<Tag> changeTags = new ArrayList<>();
        changeTags.add(TEST_TAG3);
        changeTags.add(TEST_TAG4);

        // when
        study.updateStudyTags(changeTags);

        // given
        assertThat(study.getStudyTags().size()).isEqualTo(2);
        assertThat(study.getStudyTags().get(0).getTag()).isEqualTo(TEST_TAG3);
        assertThat(study.getStudyTags().get(1).getTag()).isEqualTo(TEST_TAG4);
    }

    @Test
    @DisplayName("예외테스트 : 스터디 관리자가 아닌 회원이 스터디를 수정하면 예외가 발생한다.")
    void checkStudyAdmin(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        // when
        assertThrows(StudyException.class,()->study.checkStudyAdmin(1L));
    }

    @Test
    @DisplayName("스터디 참가 대기 인원 추가한다.")
    void addWaitUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        // when
        study.addWaitUser(2L);

        // then
        assertThat(study.getWaitUsers().size()).isEqualTo(1);
        assertThat(study.getWaitUsers().get(0).getUserId()).isEqualTo(2L);

    }

    @Test
    @DisplayName("예외테스트 : 이미 스터디 참가 대기 인원에 속한 회원이 참가 신청을 할 경우 예외가 발생한다.")
    void checkExistDuplicatedWaitUserAndStudyUser() {
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        study.addWaitUser(1L);

        // when
        assertThrows(StudyException.class,()->study.checkExistWaitUserAndStudyUser(1L));
    }

    @Test
    @DisplayName("예외테스트 : 이미 스터디 참가 인원에 속한 회원이 참가 신청을 할 경우 예외가 발생한다.")
    void checkExistWaitUserAndDuplicatedStudyUser() {
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        study.addStudyUser(1L,Role.USER);

        // when
        assertThrows(StudyException.class,()->study.checkExistWaitUserAndStudyUser(1L));
    }

    @Test
    @DisplayName("스터디 대기 인원을 삭제한다.")
    void deleteWaitUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        study.addWaitUser(1L);

        // when
        study.deleteWaitUser(1L);

        // then
        assertThat(study.getWaitUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("예외테스트 : 스터디 참가 대기 인원에 삭제할 스터디 참가 대기 인원이 없을 경우 예외가 발생한다.")
    void deleteWaitUserWhenNotExitWaitUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        // when
        assertThrows(StudyException.class,()->study.deleteWaitUser(1L));
    }

    @Test
    @DisplayName("스터디 참가 인원을 삭제한다.")
    void deleteStudyUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        study.addStudyUser(1L,Role.USER);

        // when
        study.deleteStudyUser(1L);

        // then
        assertThat(study.getStudyUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("예외테스트 : 스터디 관리자를 스터디 참가 인원에서 삭제할 경우 예외가 발생한다.")
    void deleteStudyUserAdmin(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        study.addStudyUser(1L,Role.ADMIN);

        // when
        assertThrows(StudyException.class,()->study.deleteStudyUser(1L));
    }

    @Test
    @DisplayName("예외테스트 : 스터디 참가 인원에 삭제할 스터디 참가 인원이 없을 경우 예외가 발생한다.")
    void deleteStudyUserWhenNotExistStudyUser(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        // when
        assertThrows(StudyException.class,()->study.deleteStudyUser(1L));
    }


}