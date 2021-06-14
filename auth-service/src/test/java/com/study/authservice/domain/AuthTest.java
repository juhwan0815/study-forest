package com.study.authservice.domain;

import com.study.authservice.AuthFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.study.authservice.AuthFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuthTest {

    @Test
    @DisplayName("회원 Refresh Token 변경")
    void changeRefreshToken(){
        // given
        Auth auth = new Auth(1L, 1L, null);

        // when
        auth.changeRefreshToken(TEST_REFRESH_TOKEN);

        // then
        assertThat(auth.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);
    }
}