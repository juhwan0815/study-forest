package com.study.repository;

import com.study.domain.User;
import com.study.domain.UserRole;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("카카오 ID로 조회한다.")
    void findByKakaoId(){
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        // when
        User result = userRepository.findByKakaoId(1L).get();

        // then
        assertThat(result.getKakaoId()).isEqualTo(user.getKakaoId());
    }
}
