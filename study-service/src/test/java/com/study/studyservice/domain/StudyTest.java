package com.study.studyservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StudyTest {

    @Test
    @DisplayName("스터디의 현재 인원 증가")
    void plusCurrentNumberOfPeople(){
        Category category = Category.createCategory("백엔드", null);
        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, category, studyUser, studyTagList);

        study.plusCurrentNumberOfPeople();

        assertThat(study.getCurrentNumberOfPeople()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 이미지 삭제")
    void deleteImage(){
        // given
        Category category = Category.createCategory("백엔드", null);
        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, category, studyUser, studyTagList);

        // when
        study.deleteImage();

        // then
        assertThat(study.getImageStoreName()).isNull();
        assertThat(study.getStudyImage()).isNull();
        assertThat(study.getStudyThumbnailImage()).isNull();
    }

    @Test
    @DisplayName("스터디 이미지 변경")
    void changeImage(){
        // given
        Category category = Category.createCategory("백엔드", null);
        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, category, studyUser, studyTagList);

        // when
        study.changeImage("변경된 이미지","변경된 썸네일 이미지","변경된 이미지 저장 이름");

        // then
        assertThat(study.getImageStoreName()).isEqualTo("변경된 이미지 저장 이름");
        assertThat(study.getStudyImage()).isEqualTo("변경된 이미지");
        assertThat(study.getStudyThumbnailImage()).isEqualTo("변경된 썸네일 이미지");

    }

    @Test
    @DisplayName("스터디 수정")
    void update(){
        // given
        Category category = Category.createCategory("백엔드", null);
        Category changeCategory = Category.createCategory("프론트엔드", null);
        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTestTag(1L,"스프링");
        Tag tag2 = Tag.createTestTag(2L,"JPA");
        tagList.add(tag1);
        tagList.add(tag2);

        List<Tag> changeTagList = new ArrayList<>();
        Tag tag3 = Tag.createTestTag(3L,"노드");
        Tag tag4 = Tag.createTestTag(4L,"sequlize");
        changeTagList.add(tag3);
        changeTagList.add(tag4);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, category, studyUser, studyTagList);

        study.update("노드 스터디",10,"안녕하세요 노드 스터디입니다.",
                false,false,true,2L,changeCategory,changeTagList);

        assertThat(study.getName()).isEqualTo("노드 스터디");
        assertThat(study.getNumberOfPeople()).isEqualTo(10);
        assertThat(study.getContent()).isEqualTo("안녕하세요 노드 스터디입니다.");
        assertThat(study.isOnline()).isEqualTo(false);
        assertThat(study.isOffline()).isEqualTo(false);
        assertThat(study.getStatus()).isEqualTo(StudyStatus.CLOSE);
        assertThat(study.getLocationId()).isEqualTo(2L);
        assertThat(study.getStudyTags().size()).isEqualTo(2);
        assertThat(study.getCategory()).isEqualTo(changeCategory);
    }

}