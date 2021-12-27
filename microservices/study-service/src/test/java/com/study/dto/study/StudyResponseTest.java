package com.study.dto.study;

import com.study.domain.Study;
import com.study.domain.StudyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class StudyResponseTest {

    @Test
    @DisplayName("스터디 응답을 반환한다.")
    void from() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addTags(Arrays.asList("스프링"," JPA"));

        // when
        StudyResponse result = StudyResponse.from(study);

        // then
        assertThat(result.getName()).isEqualTo(study.getName());
        assertThat(result.getContent()).isEqualTo(study.getContent());
        assertThat(result.getNumberOfPeople()).isEqualTo(study.getNumberOfPeople());
        assertThat(result.isOnline()).isEqualTo(study.isOnline());
        assertThat(result.isOffline()).isEqualTo(study.isOffline());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getImageUrl()).isNull();
    }

    @Test
    @DisplayName("스터디 + 태그 + 대기상태 응답을 반환한다.")
    void fromWithWaitUserAndTag() {
        // given
        Study study = Study.createStudy("스프링 스터디", "스프링 스터디", 5, true, true, null);
        study.addWaitUser(1L);
        study.addTags(Arrays.asList("스프링"," JPA"));

        // when
        StudyResponse result = StudyResponse.fromWithWaitUserAndTag(study);

        // then
        assertThat(result.getName()).isEqualTo(study.getName());
        assertThat(result.getContent()).isEqualTo(study.getContent());
        assertThat(result.getNumberOfPeople()).isEqualTo(study.getNumberOfPeople());
        assertThat(result.isOnline()).isEqualTo(study.isOnline());
        assertThat(result.isOffline()).isEqualTo(study.isOffline());
        assertThat(result.getStatus()).isEqualTo(StudyStatus.OPEN);
        assertThat(result.getImageUrl()).isNull();
        assertThat(result.getTags().size()).isEqualTo(2);
    }
}