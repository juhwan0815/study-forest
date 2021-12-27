package com.study.repository;

import com.study.domain.Study;
import com.study.domain.StudyRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void init() {
        studyRepository.deleteAll();
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 스터디 회원을 같이 조화한다.")
    void findWithStudyUserById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addStudyUser(1L, StudyRole.ADMIN);
        study.addStudyUser(2L, StudyRole.ADMIN);
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        Study result = studyRepository.findWithStudyUserById(study.getId()).get();

        // then
        assertThat(result.getStudyUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 참가 대기 회원을 같이 조회한다.")
    void findWithWaitUserById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addWaitUser(1L);
        study.addWaitUser(2L);
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        Study result = studyRepository.findWithWaitUserById(study.getId()).get();

        // then
        assertThat(result.getWaitUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 ID 로 스터디와 채팅방을 같이 조회한다.")
    void findWithChatRoomById() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addChatRoom("공지사항");
        study.addChatRoom("대화방");
        studyRepository.save(study);

        em.flush();
        em.clear();
        // when
        Study result = studyRepository.findWithChatRoomById(study.getId()).get();

        // then
        assertThat(result.getChatRooms().size()).isEqualTo(2);
    }
}