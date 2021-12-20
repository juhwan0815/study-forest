package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTest {

    @Test
    @DisplayName("인증 정보를 생성한다.")
    void createAuth() {
        // given
        Long userId = 1L;
        String refreshToken = "refreshToken";
        Long expiration = 30L;

        // when
        Auth result = Auth.createAuth(1L, refreshToken, expiration);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(result.getExpiration()).isEqualTo(expiration);
    }
}
