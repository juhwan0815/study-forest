package com.study.repository;

import com.study.domain.Category;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("스터디 ID 로 스터디와 스터디 회원을 같이 조화한다.")
    void findWithStudyUserById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.ADMIN);
        studyRepository.save(study);

        // when
        Study result = studyRepository.findWithStudyUserById(study.getId()).get();

        // then
        assertThat(result).isEqualTo(study);
        assertThat(result.getStudyUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 참가 대기 회원을 같이 조회한다.")
    void findWithWaitUserById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addWaitUser(1L);
        study.addWaitUser(2L);
        studyRepository.save(study);

        // when
        Study result = studyRepository.findWithWaitUserById(study.getId()).get();

        // then
        assertThat(result).isEqualTo(study);
        assertThat(result.getWaitUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 채팅방을 같이 조회한다.")
    void findWithChatRoomById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addChatRoom("공지사항");
        study.addChatRoom("대화방");
        studyRepository.save(study);

        // when
        Study result = studyRepository.findWithChatRoomById(study.getId()).get();

        // then
        assertThat(result).isEqualTo(study);
        assertThat(result.getChatRooms().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("채팅방 ID로 스터디를 조회한다.")
    void findByChatRoomId() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.addChatRoom("공지사항");
        studyRepository.save(study);

        // when
        Study result = studyRepository.findByChatRoomId(study.getChatRooms().get(0).getId()).get();

        // then
        assertThat(result).isEqualTo(study);
        assertThat(result.getChatRooms().size()).isEqualTo(1);
    }


    @Test
    @DisplayName("스터디와 태그를 같이 조회한다.")
    void findWithTagById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", null);
        study.changeTags(Arrays.asList("스프링", "JPA"));
        studyRepository.save(study);

        // when
        Study result = studyRepository.findWithTagById(study.getId()).get();

        // then
        assertThat(result).isEqualTo(study);
        assertThat(result.getTags().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 카테고리, 태그를 같이 조회한다.")
    void findWithCategoryAndTagById() {
        // given
        Category category = Category.createCategory("개발", null);
        categoryRepository.save(category);
        Category childCategory = Category.createCategory("백엔드", category);
        categoryRepository.save(childCategory);

        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, false, false, "imageUrl", childCategory);
        study.changeTags(Arrays.asList("스프링", "백엔드"));
        studyRepository.save(study);

        // when
        Study result = studyRepository.findWithCategoryAndTagById(study.getId()).get();

        // then
        assertThat(result).isEqualTo(study);
        assertThat(result.getCategory()).isEqualTo(childCategory);
        assertThat(result.getCategory().getParent()).isEqualTo(category);
        assertThat(result.getTags().size()).isEqualTo(2);
    }

}