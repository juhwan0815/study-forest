package com.study.studyservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.studyservice.domain.*;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.repository.StudyRepository;
import com.study.studyservice.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyQueryRepositoryTest {

    @Autowired
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private StudyQueryRepository studyQueryRepository;

    @BeforeEach
    void setUp(){
        queryFactory = new JPAQueryFactory(em);
        studyQueryRepository = new StudyQueryRepository(queryFactory);
    }

    @Test
    @DisplayName("스터디와 스터디 태그를 같이 조회한다.")
    void findWithStudyTagById(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.createTag("JPA"));
        tags.add(Tag.createTag("스프링"));
        tagRepository.saveAll(tags);

        study.addStudyTags(tags);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study result = studyQueryRepository.findWithStudyTagsById(savedStudy.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedStudy.getId());
        assertThat(result.getStudyTags().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디와 스터디 유저를 같이 조회한다.")
    void findWithStudyUsersById(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        study.addStudyUser(1L,Role.ADMIN);
        study.addStudyUser(2L,Role.USER);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study result = studyQueryRepository.findWithStudyUsersById(savedStudy.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedStudy.getId());
        assertThat(result.getStudyUsers().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("스터디와 카테고리,스터디태그와 태그를 같이 조회한다.")
    void findWithCategoryAndStudyTagAndTagById(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        categoryRepository.save(parentCategory);
        Category childCategory = Category.createCategory("백엔드", parentCategory);
        categoryRepository.save(childCategory);

        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, childCategory);

        List<Tag> tags = new ArrayList<>();
        Tag tag1 = Tag.createTag("JPA");
        Tag tag2 = Tag.createTag("스프링");
        tags.add(tag1);
        tags.add(tag2);
        tagRepository.saveAll(tags);

        study.addStudyTags(tags);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study result = studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(savedStudy.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedStudy.getId());
        assertThat(result.getCategory().getId()).isEqualTo(childCategory.getId());
        assertThat(result.getCategory().getParent().getId()).isEqualTo(parentCategory.getId());
        assertThat(result.getStudyTags().size()).isEqualTo(2);
        assertThat(result.getStudyTags().get(0).getTag().getName()).isEqualTo("스프링");
        assertThat(result.getStudyTags().get(1).getTag().getName()).isEqualTo("JPA");
    }

    @Test
    @DisplayName("스터디와 스터디 가입 대기 유저를 같이 조회한다.")
    void findWithWaitUsersById(){
        // given
        Study study = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        study.addWaitUser(1L);
        study.addWaitUser(2L);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study result = studyQueryRepository.findWithWaitUserById(savedStudy.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedStudy.getId());
        assertThat(result.getWaitUsers().size()).isEqualTo(2);
        assertThat(result.getWaitUsers().get(0).getUserId()).isEqualTo(1L);
        assertThat(result.getWaitUsers().get(1).getUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("스터디 ID로 스터디 목록을 조회한다.")
    void findByIdIn(){
        Study study1 = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);
        Study study2 = Study.createStudy("테스트 스터디", 5, "테스트 스터디입니다.",
                true, true, null);

        studyRepository.save(study1);
        studyRepository.save(study2);

        em.flush();
        em.clear();

        List<Study> result = studyQueryRepository.findByIdIn(Arrays.asList(study1.getId(), study2.getId()));

        assertThat(result.size()).isEqualTo(2);
    }
}