package com.study.domain;

import com.study.exception.GatheringUserDuplicateException;
import com.study.exception.GatheringUserNotFoundException;
import com.study.exception.NotRegisterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GatheringTest {

    @Test
    @DisplayName("모임을 생성한다.")
    void createGathering() {
        // given
        long studyId = 1L;
        LocalDateTime localDateTime = LocalDateTime.now();
        boolean offline = true;
        String content = "오프라인 모임입니다.";

        // when
        Gathering result = Gathering.createGathering(studyId, localDateTime, offline, content);

        // then
        assertThat(result.getStudyId()).isEqualTo(studyId);
        assertThat(result.getGatheringTime()).isEqualTo(localDateTime);
        assertThat(result.isOffline()).isEqualTo(offline);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getNumberOfPeople()).isEqualTo(0);
    }

    @Test
    @DisplayName("모임을 수정한다.")
    void update() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        LocalDateTime updateTime = LocalDateTime.now();

        // when
        gathering.update(updateTime, false, "온라인 모임");

        // then
        assertThat(gathering.getGatheringTime()).isEqualTo(updateTime);
        assertThat(gathering.isOffline()).isEqualTo(false);
        assertThat(gathering.getContent()).isEqualTo("온라인 모임");
    }

    @Test
    @DisplayName("모임의 장소를 변경한다.")
    void changePlace() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        Place place = Place.createPlace("스터디 카페", 37.584009, 126.970626);

        // when
        gathering.changePlace(place);

        // then
        assertThat(gathering.getPlace()).isEqualTo(place);
    }

    @Test
    @DisplayName("모임 참가자를 추가한다.")
    void addGatheringUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");

        // when
        gathering.addGatheringUser(1L, true);

        // then
        assertThat(gathering.getGatheringUsers().size()).isEqualTo(1);
        assertThat(gathering.getNumberOfPeople()).isEqualTo(1);
    }

    @Test
    @DisplayName("예외 테스트 : 중복된 모임 참가자를 추가하면 예외가 발생한다.")
    void addDuplicateGatheringUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        // when
        assertThrows(GatheringUserDuplicateException.class, () -> gathering.addGatheringUser(1L, true));
    }

    @Test
    @DisplayName("모임 참가자를 삭제한다.")
    void deleteGatheringUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        // when
        gathering.deleteGatheringUser(1L);

        // then
        assertThat(gathering.getGatheringUsers().size()).isEqualTo(0);
        assertThat(gathering.getNumberOfPeople()).isEqualTo(0);
    }

    @Test
    @DisplayName("예외 테스트 : 존재하지 않는 모임 참가자를 삭제하면 예외가 발생한다.")
    void deleteNotExistGatheringUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");

        // when
        assertThrows(GatheringUserNotFoundException.class, () -> gathering.deleteGatheringUser(1L));
    }

    @Test
    @DisplayName("모임 등록자인지 확인한다.")
    void isRegister() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        // when
        gathering.isRegister(1L);
    }

    @Test
    @DisplayName("예외 테스트 : 모임 등록자인지 확인하고 아니면 예외가 발생한다.")
    void isNotRegister() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, false);

        // when
        assertThrows(NotRegisterException.class, () -> gathering.isRegister(1L));
    }

    @Test
    @DisplayName("예외 테스트 : 모임 등록자인지 확인하고 확인한 회원이 없으면 예외가 발생한다.")
    void isNotExistRegister() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, false);

        // when
        assertThrows(NotRegisterException.class, () -> gathering.isRegister(2L));
    }

    @Test
    @DisplayName("모임 참가자의 ID 리스트를 반환한다.")
    void getGatheringUserId() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, false);

        // when
        List<Long> result = gathering.getGatheringUserId();

        // then
        assertThat(result.size()).isEqualTo(1);
    }
}