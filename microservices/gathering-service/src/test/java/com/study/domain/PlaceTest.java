package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceTest {

    @Test
    @DisplayName("모임의 장소를 생성한다.")
    void createPlace() {
        // given
        String name = "스터디 카페";
        double let = 37.584009;
        double len = 126.970626;

        // when
        Place result = Place.createPlace(name, let, len);

        // then
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getLet()).isEqualTo(let);
        assertThat(result.getLen()).isEqualTo(len);
    }
}