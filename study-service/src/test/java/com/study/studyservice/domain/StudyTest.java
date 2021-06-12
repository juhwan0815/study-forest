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

}