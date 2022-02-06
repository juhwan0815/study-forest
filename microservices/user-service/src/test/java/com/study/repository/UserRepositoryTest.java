package com.study.repository;

import com.study.domain.User;
import com.study.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("카카오 ID로 조회한다.")
    void findByKakaoId() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", "imageUrl", UserRole.USER);
        userRepository.save(user);

        // when
        User result = userRepository.findByKakaoId(1L).get();

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("회원 ID 리스트로 조회한다.")
    void findByIdIn() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", "imageUrl", UserRole.USER);
        userRepository.save(user);

        // when
        List<User> result = userRepository.findByIdIn(Arrays.asList(user.getId()));

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원 Id로 회원과 관심 키워드를 조회한다.")
    void findWithKeywordById() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", "imageUrl", UserRole.USER);
        user.addKeyword("개발");
        user.addKeyword("토익");
        userRepository.save(user);

        // when
        User findUser = userRepository.findWithKeywordById(user.getId()).get();

        // then
        assertThat(findUser.getKeywords().size()).isEqualTo(2);
    }
}
