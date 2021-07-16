package com.study.gatheringservice.domain;

import com.study.gatheringservice.exception.GatheringException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("모임 등록자 여부를 확인한다.")
    void checkRegister(){
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), Shape.OFFLINE, "스프링 스터디입니다.");
        gathering.addGatheringUser(1L,true);
        gathering.addGatheringUser(2L,false);

        // when
        gathering.checkRegister(1L);
    }

    @Test
    @DisplayName("예외 테스트 : 모임 등록자 여부를 확인하고 아닐 경우 예외가 발생한다.")
    void checkRegisterWhenNotRegister(){
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), Shape.OFFLINE, "스프링 스터디입니다.");
        gathering.addGatheringUser(1L,true);
        gathering.addGatheringUser(2L,false);

        // when
        assertThrows(GatheringException.class,()->gathering.checkRegister(2L));
    }

    @Test
    @DisplayName("모임을 수정한다.")
    void update(){
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), Shape.OFFLINE, "스프링 스터디입니다.");

        // when
        LocalDateTime now = LocalDateTime.now();
        gathering.update(now,Shape.ONLINE,"테스트 스터디");

        // then
        assertThat(gathering.getGatheringTime()).isEqualTo(now);
        assertThat(gathering.getShape()).isEqualTo(Shape.ONLINE);
        assertThat(gathering.getContent()).isEqualTo("테스트 스터디");
    }

}

