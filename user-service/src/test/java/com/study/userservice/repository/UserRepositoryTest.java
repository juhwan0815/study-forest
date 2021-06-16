package com.study.userservice.repository;

import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("카카오 ID로 회원을 조회한다.")
    void findByKakaoId(){
        // given
        User user = User.createUser(1L,"황주환","10~19","male", UserRole.USER);
        userRepository.save(user);

        em.flush();
        em.clear();

        // when
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).get();

        // then
        assertThat(findUser.getKakaoId()).isEqualTo(user.getKakaoId());
    }


}