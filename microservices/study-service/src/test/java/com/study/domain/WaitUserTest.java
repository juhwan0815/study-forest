package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WaitUserTest {

    @Test
    @DisplayName("스터디 참가 대기자를 생성한다.")
    void createWaitUser() {
        // given
        Long userId = 1L;

        // when
        WaitUser result = WaitUser.createWaitUser(userId, null);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(WaitStatus.WAIT);
    }

    @Test
    @DisplayName("스터디 참가 대기자의 상태를 실패로 수정한다.")
    void fail() {
        // given
        WaitUser waitUser = WaitUser.createWaitUser(1L, null);

        // when
        waitUser.fail();

        // then
        assertThat(waitUser.getStatus()).isEqualTo(WaitStatus.FAIL);
    }

    @Test
    @DisplayName("스터디 참가 대기자의 상태를 성공으로 수정한다.")
    void success() {
        // given
        WaitUser waitUser = WaitUser.createWaitUser(1L,  null);

        // when
        waitUser.success();

        // then
        assertThat(waitUser.getStatus()).isEqualTo(WaitStatus.SUCCESS);
    }
}