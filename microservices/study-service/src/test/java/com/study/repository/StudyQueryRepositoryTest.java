package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Category;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import com.study.dto.study.StudySearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.study.fixture.CategoryFixture.TEST_CATEGORY;
import static com.study.fixture.CategoryFixture.TEST_CHILD_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyQueryRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    private StudyQueryRepository studyQueryRepository;

    @BeforeEach
    public void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        studyQueryRepository = new StudyQueryRepository(queryFactory);
        studyRepository.deleteAll();
    }

    @Test
    @DisplayName("채팅방 ID로 스터디를 조회한다.")
    void findByChatRoomId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addChatRoom("공지사항");
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        Study findStudy = studyQueryRepository.findByChatRoomId(study.getChatRooms().get(0).getId());

        // then
        assertThat(findStudy.getName()).isEqualTo("스프링 스터디");
        assertThat(findStudy.getContent()).isEqualTo("스프링 스터디");
        assertThat(findStudy.getNumberOfPeople()).isEqualTo(5);
        assertThat(findStudy.isOffline()).isTrue();
        assertThat(findStudy.isOffline()).isTrue();
    }

    @Test
    @DisplayName("예외 테스트 : 존재하지 않는 채팅방 ID로 스터디를 조회하면 예외가 발생한다..")
    void findByChatRoomIdNotExist() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        studyRepository.save(study);

        em.flush();
        em.clear();

        // when
        assertThrows(RuntimeException.class,()-> studyQueryRepository.findByChatRoomId(1L));
    }

    @Test
    @DisplayName("회원 ID로 스터디와 참가 대기자를 같이 조회한다.")
    void findWithWaitUserByUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addWaitUser(1L);
        study.addWaitUser(2L);
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        List<Study> result = studyQueryRepository.findWithWaitUserByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getWaitUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원 ID 스터디와 스터디 참가자를 같이 조회한다.")
    void findWithStudyUsersByUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        List<Study> result = studyQueryRepository.findWithStudyUsersByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getStudyUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 케타고리, 태그를 같이 조회한다.")
    void findWithCategoryAndTagById() {
        // given
        Category category = Category.createCategory("개발", null);
        categoryRepository.save(category);
        Category childCategory = Category.createCategory("백엔드", category);
        categoryRepository.save(childCategory);

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, childCategory);
        study.addTags(Arrays.asList("스프링", "백엔드"));
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        Study result = studyQueryRepository.findWithCategoryAndTagById(study.getId());

        // then
        assertThat(result.getCategory().getName()).isEqualTo(TEST_CHILD_CATEGORY.getName());
        assertThat(result.getCategory().getParent().getName()).isEqualTo(TEST_CATEGORY.getName());
        assertThat(result.getTags().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("예외 테스트 : 존재하지 않는 ID 로 스터디와 카테고리, 태그를 같이 조회하면 예외가 발생한다.")
    void findWithCategoryAndTagByNotExistId() {
        // given
        Category category = Category.createCategory("개발", null);
        categoryRepository.save(category);
        Category childCategory = Category.createCategory("백엔드", category);
        categoryRepository.save(childCategory);

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, childCategory);
        study.addTags(Arrays.asList("스프링", "백엔드"));
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        assertThrows(RuntimeException.class, () -> studyQueryRepository.findWithCategoryAndTagById(100L));
    }


    @Test
    @DisplayName("회원 ID 로 스터디와 태그를 같이 조회한다.")
    void findByUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);
        study.addTags(Arrays.asList("스프링", "백엔드"));
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        List<Study> result = studyQueryRepository.findByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTags().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("모든 검색 조건으로 스터디를 검색한다.")
    void findBySearchFullCondition() {
        // given
        Category category = Category.createCategory("개발", null);
        categoryRepository.save(category);

        List<Long> areaIds = new ArrayList<>();
        IntStream.range(0, 20).forEach(value -> {
            Study study = Study.createStudy("스프링" + value, "스터디입니다.", 5, true, true, category);

            Long areaId = 800L + Long.valueOf(value);
            areaIds.add(areaId);

            study.changeArea(areaId);
            studyRepository.save(study);
        });

        StudySearchRequest studySearchRequest = new StudySearchRequest("스프링", category.getId(), true, true);
        em.flush();
        em.clear();

        // when
        Page<Study> result = studyQueryRepository
                .findBySearchCondition(studySearchRequest, areaIds, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("검색 조건 없이 스터디를 검색한다.")
    void findBySearchNoCondition() {
        // given
        List<Long> areaIds = null;
        IntStream.range(0, 20).forEach(value -> {
            Study study = Study.createStudy("스프링" + value, "스터디입니다.", 5, true, true, null);
            studyRepository.save(study);
        });

        StudySearchRequest studySearchRequest = new StudySearchRequest(null, null, null, null);
        em.flush();
        em.clear();

        // when
        Page<Study> result = studyQueryRepository
                .findBySearchCondition(studySearchRequest, areaIds, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent().size()).isEqualTo(10);
    }

}