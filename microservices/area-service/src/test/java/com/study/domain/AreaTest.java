package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AreaTest {

    @Test
    @DisplayName("지역을 생성한다.")
    void createArea() {
        // given
        String code = "code";
        String city = "서울특별시";
        String gu = "종로구";
        String dong = "청운효자동";
        double let = 37.590758;
        double len = 126.980996;
        String codeType = "H";

        // when
        Area result = Area.createArea(code, city, gu, dong, null, let, len, codeType);

        // then
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getCity()).isEqualTo(city);
        assertThat(result.getGu()).isEqualTo(gu);
        assertThat(result.getDong()).isEqualTo(dong);
        assertThat(result.getRi()).isNull();
        assertThat(result.getLet()).isEqualTo(let);
        assertThat(result.getLen()).isEqualTo(len);
        assertThat(result.getCodeType()).isEqualTo(codeType);
    }
}
