package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Category;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

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
    }

    @Test
    @DisplayName("참가 대기자 ID로 스터디와 태그를 같이 조회한다.")
    void findByWaitUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.changeTags(Arrays.asList("스프링"));
        study.addWaitUser(1L);
        study.addWaitUser(2L);
        studyRepository.save(study);

        // when
        List<StudyResponse> result = studyQueryRepository.findByWaitUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTags().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 참가자 ID로 스터디와 태그를 같이 조회한다.")
    void findWithStudyUsersByUserId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.changeTags(Arrays.asList("스프링"));
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.USER);
        studyRepository.save(study);

        // when
        List<StudyResponse> result = studyQueryRepository.findByStudyUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTags().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("모든 검색 조건으로 스터디를 검색한다.")
    void findBySearchFullCondition() {
        // given
        Category category = Category.createCategory("개발", null);
        categoryRepository.save(category);

        List<Long> areaIds = new ArrayList<>();
        IntStream.range(0, 20).forEach(value -> {
            Study study = Study.createStudy("스프링" + value, "스터디입니다.", 5, true, true, "imageUrl", category);

            Long areaId = 800L + Long.valueOf(value);
            areaIds.add(areaId);

            study.changeArea(areaId);
            studyRepository.save(study);
        });

        StudySearchRequest studySearchRequest = new StudySearchRequest("스프링", category.getId(), true, true, 10, 11L);

        // when
        List<StudyResponse> result = studyQueryRepository.findBySearchCondition(studySearchRequest, areaIds);

        // then
        assertThat(result.size()).isEqualTo(9);
    }

    @Test
    @DisplayName("검색 조건 없이 스터디를 검색한다.")
    void findBySearchNoCondition() {
        // given
        List<Long> areaIds = null;
        IntStream.range(0, 20).forEach(value -> {
            Study study = Study.createStudy("스프링" + value, "스터디입니다.", 5, true, true, "imageUrl", null);
            studyRepository.save(study);
        });

        StudySearchRequest studySearchRequest = new StudySearchRequest(null, null, null, null, 10, null);

        // when
        List<StudyResponse> result = studyQueryRepository
                .findBySearchCondition(studySearchRequest, areaIds);

        // then
        assertThat(result.size()).isEqualTo(10);
    }

}