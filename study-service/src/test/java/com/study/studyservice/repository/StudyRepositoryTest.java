package com.study.studyservice.repository;

import com.study.studyservice.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.study.studyservice.fixture.StudyFixture.*;
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
    private EntityManager em;

    @Test
    @DisplayName("스터디 생성 테스트")
    void create(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.createTag("JPA"));
        tags.add(Tag.createTag("스프링"));
        tagRepository.saveAll(tags);

        study.addStudyTags(tags);
        study.addStudyUser(1L,Role.ADMIN);
        study.changeImage(TEST_IMAGE);
        study.changeLocation(1L);

        // when
        Study savedStudy = studyRepository.save(study);

        // then
        assertThat(savedStudy.getId()).isNotNull();
    }

    @Test
    @DisplayName("스터디 삭제")
    void delete(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.createTag("JPA"));
        tags.add(Tag.createTag("스프링"));
        tagRepository.saveAll(tags);

        study.addStudyTags(tags);
        study.addStudyUser(1L,Role.ADMIN);
        study.changeImage(TEST_IMAGE);
        study.changeLocation(1L);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        studyRepository.delete(savedStudy);

        // then
        assertThat(studyRepository.findById(1L)).isEmpty();
    }
//
}