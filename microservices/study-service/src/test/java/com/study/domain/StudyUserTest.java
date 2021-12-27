package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudyUserTest {

    @Test
    @DisplayName("스터디 참가자를 생성한다.")
    void createStudyUser() {
        // given
        Long userId = 1L;
        StudyRole studyRole = StudyRole.USER;

        // when
        StudyUser result = StudyUser.createStudyUser(userId, studyRole, null);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStudyRole()).isEqualTo(studyRole);
    }
}