package com.study.studyservice.repository;

import com.study.studyservice.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("스터디 생성 테스트")
    void save(){
        // given
        Category category = Category.createCategory("백엔드", null);
        categoryRepository.save(category);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        tagList.add(tag1);
        tagList.add(tag2);
        tagRepository.saveAll(tagList);

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
        Study savedStudy = studyRepository.save(study);

        // then
        assertThat(savedStudy.getId()).isNotNull();
    }

    @Test
    @DisplayName("스터디 삭제")
    void delete(){
        // given
        Category category = Category.createCategory("백엔드", null);
        categoryRepository.save(category);

        StudyUser studyUser = StudyUser.createStudyUser(1L, Role.ADMIN);

        List<Tag> tagList = new ArrayList<>();
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("JPA");
        tagList.add(tag1);
        tagList.add(tag2);
        tagRepository.saveAll(tagList);

        List<StudyTag> studyTagList = new ArrayList<>();
        StudyTag studyTag1 = StudyTag.createStudyTag(tag1);
        StudyTag studyTag2 = StudyTag.createStudyTag(tag2);
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);

        Study study = Study.createStudy("스프링 스터디",
                5, "안녕하세요 스프링 스터디입니다.",
                true, true, "이미지 저장 이름",
                "이미지", "썸네일 이미지", 1L, category, studyUser, studyTagList);

        Study savedStudy = studyRepository.save(study);

        // when
        studyRepository.delete(savedStudy);

        // then
        assertThat(studyRepository.findById(1L)).isEmpty();
    }

}