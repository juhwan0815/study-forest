package com.study.authservice.repository;

import com.study.authservice.AuthFixture;
import com.study.authservice.domain.Auth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;

import javax.persistence.EntityManager;

import static com.study.authservice.AuthFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("회원 ID로 조회")
    void findByUserId(){
        // given
        authRepository.save(Auth.createAuth(1L, TEST_REFRESH_TOKEN));

        em.flush();
        em.clear();

        // when
        Auth result = authRepository.findByUserId(1L).get();

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);

    }

}