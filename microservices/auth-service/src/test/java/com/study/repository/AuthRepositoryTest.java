package com.study.repository;

import com.study.config.EmbeddedRedisConfig;
import com.study.domain.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(EmbeddedRedisConfig.class)
@DataRedisTest
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @BeforeEach
    public void tearDown() {
        authRepository.deleteAll();
    }

    @Test
    @DisplayName("인증 정보를 저장한다.")
    void save(){
        // given
        Auth auth = Auth.createAuth(1L, "refreshToken", 30L);

        // when
        authRepository.save(auth);

        // then
        Auth result = authRepository.findById(auth.getUserId()).get();
        assertThat(result.getRefreshToken()).isEqualTo(auth.getRefreshToken());
        assertThat(result.getExpiration()).isLessThan(auth.getExpiration());
    }

    @Test
    @DisplayName("회원 ID로 인증 정보를 조회한다.")
    void findById(){
        // given
        Auth auth = Auth.createAuth(1L, "refreshToken", 30L);
        authRepository.save(auth);

        // when
        Auth result = authRepository.findById(auth.getUserId()).get();

        // then
        assertThat(result.getRefreshToken()).isEqualTo(auth.getRefreshToken());
        assertThat(result.getExpiration()).isLessThan(auth.getExpiration());
    }


}