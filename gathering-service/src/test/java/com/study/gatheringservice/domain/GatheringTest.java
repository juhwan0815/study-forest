package com.study.gatheringservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.comparable;

class GatheringTest {

    @Test
    @DisplayName("모임의 장소를 변경한다.")
    void changePlace(){
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), Shape.OFFLINE, "스프링 스터디입니다.");
        Place place = Place.createPlace("인천광역시 부평구 산곡4동", 37.584009, 126.970626);

        // when
        gathering.changePlace(place);

        // then
        assertThat(gathering.getPlace()).isEqualTo(place);
    }

    @Test
    @DisplayName("모임의 참가자를 추가한다.")
    void addGatheringUser(){
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), Shape.OFFLINE, "스프링 스터디입니다.");

        // when
        gathering.addGatheringUser(1L,true);

        // then
        assertThat(gathering.getNumberOfPeople()).isEqualTo(1);
        assertThat(gathering.getGatheringUsers().size()).isEqualTo(1);
    }
}

