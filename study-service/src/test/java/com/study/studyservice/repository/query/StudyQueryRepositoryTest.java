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
    @DisplayName("스터디 ID 조회 + 스터디 태그")
    void findWithStudyTagById(){
        // given
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
                "이미지", "썸네일 이미지", 1L, null, studyUser, studyTagList);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study findStudy = studyQueryRepository.findWithStudyTagsById(savedStudy.getId());

        // then
        assertThat(findStudy.getId()).isEqualTo(savedStudy.getId());
        assertThat(findStudy.getName()).isEqualTo("스프링 스터디");
        assertThat(findStudy.getNumberOfPeople()).isEqualTo(5);
        assertThat(findStudy.getContent()).isEqualTo("안녕하세요 스프링 스터디입니다.");
        assertThat(findStudy.isOnline()).isEqualTo(true);
        assertThat(findStudy.isOffline()).isEqualTo(true);
        assertThat(findStudy.getImageStoreName()).isEqualTo("이미지 저장 이름");
        assertThat(findStudy.getStudyImage()).isEqualTo("이미지");
        assertThat(findStudy.getStudyThumbnailImage()).isEqualTo("썸네일 이미지");
        assertThat(findStudy.getLocationId()).isEqualTo(1L);
        assertThat(findStudy.getStudyTags().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 ID 조회 + 스터디 유저")
    void findWithStudyUserById(){
        // given
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
                "이미지", "썸네일 이미지", 1L, null, studyUser, studyTagList);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study findStudy = studyQueryRepository.findWithStudyUsersById(savedStudy.getId());

        // then
        assertThat(findStudy.getId()).isEqualTo(savedStudy.getId());
        assertThat(findStudy.getName()).isEqualTo("스프링 스터디");
        assertThat(findStudy.getNumberOfPeople()).isEqualTo(5);
        assertThat(findStudy.getContent()).isEqualTo("안녕하세요 스프링 스터디입니다.");
        assertThat(findStudy.isOnline()).isEqualTo(true);
        assertThat(findStudy.isOffline()).isEqualTo(true);
        assertThat(findStudy.getImageStoreName()).isEqualTo("이미지 저장 이름");
        assertThat(findStudy.getStudyImage()).isEqualTo("이미지");
        assertThat(findStudy.getStudyThumbnailImage()).isEqualTo("썸네일 이미지");
        assertThat(findStudy.getLocationId()).isEqualTo(1L);
        assertThat(findStudy.getStudyUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 ID 조회 + 스터디 태그 + 태그 + 자식 카테고리 + 부모 카테고리")
    void findWithCategoryAndStudyTagAndTagById(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        categoryRepository.save(parentCategory);

        Category childCategory = Category.createCategory("백엔드", parentCategory);
        categoryRepository.save(childCategory);

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
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study findStudy = studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(savedStudy.getId());

        // then
        assertThat(findStudy.getId()).isEqualTo(savedStudy.getId());
        assertThat(findStudy.getName()).isEqualTo("스프링 스터디");
        assertThat(findStudy.getNumberOfPeople()).isEqualTo(5);
        assertThat(findStudy.getContent()).isEqualTo("안녕하세요 스프링 스터디입니다.");
        assertThat(findStudy.isOnline()).isEqualTo(true);
        assertThat(findStudy.isOffline()).isEqualTo(true);
        assertThat(findStudy.getImageStoreName()).isEqualTo("이미지 저장 이름");
        assertThat(findStudy.getStudyImage()).isEqualTo("이미지");
        assertThat(findStudy.getStudyThumbnailImage()).isEqualTo("썸네일 이미지");
        assertThat(findStudy.getLocationId()).isEqualTo(1L);
        assertThat(findStudy.getStudyTags().size()).isEqualTo(2);
        assertThat(findStudy.getStudyTags().get(0).getTag().getName()).isEqualTo("스프링");
        assertThat(findStudy.getStudyTags().get(1).getTag().getName()).isEqualTo("JPA");
        assertThat(findStudy.getCategory().getName()).isEqualTo("백엔드");
        assertThat(findStudy.getCategory().getParent().getName()).isEqualTo("개발");
    }

    @Test
    @DisplayName("스터디 ID 조회 + 스터디 가입 신청 유저")
    void findWithWaitUsersById(){

        // given
        Category parentCategory = Category.createCategory("개발", null);
        categoryRepository.save(parentCategory);

        Category childCategory = Category.createCategory("백엔드", parentCategory);
        categoryRepository.save(childCategory);

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
                "이미지", "썸네일 이미지", 1L, childCategory, studyUser, studyTagList);

        study.addWaitUser(2L);
        study.addWaitUser(3L);

        Study savedStudy = studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        Study result = studyQueryRepository.findWithWaitUserById(savedStudy.getId());

        // then
        assertThat(result.getId()).isEqualTo(savedStudy.getId());
        assertThat(result.getWaitUsers().size()).isEqualTo(2);
        assertThat(result.getWaitUsers().get(0).getUserId()).isEqualTo(2L);
        assertThat(result.getWaitUsers().get(1).getUserId()).isEqualTo(3L);
    }

}