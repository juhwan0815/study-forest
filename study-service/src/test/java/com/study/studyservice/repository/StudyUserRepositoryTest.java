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
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyUserRepositoryTest {

    @Autowired
    private StudyUserRepository studyUserRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("스터디 관리자 회원 조회")
    void findByUserIdAndRoleAndStudy(){
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

        studyRepository.save(study);

        // when
        StudyUser result = studyUserRepository.findByUserIdAndAndRoleAndStudy(1L, Role.ADMIN, study).get();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.getStudy()).isEqualTo(study);
    }

}