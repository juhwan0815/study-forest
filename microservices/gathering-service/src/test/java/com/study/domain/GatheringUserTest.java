package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringUserTest {

    @Test
    @DisplayName("모임 참가자를 생성한다.")
    void createGatheringUser() {
        // given
        long userId = 1L;
        boolean register = true;

        // when
        GatheringUser result = GatheringUser.createGatheringUser(userId, register, null);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.isRegister()).isEqualTo(register);
    }
}