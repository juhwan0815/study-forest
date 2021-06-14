package com.study.userservice.repository;

import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 저장")
    void saveUser(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);

        // when
        User savedUser = userRepository.save(user);

        // then
        assertNotNull(savedUser.getId());
    }

    @Test
    @DisplayName("카카오 ID로 회원 조회")
    void findByKakaoId(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        userRepository.save(user);

        // when
        User findUser = userRepository.findByKakaoId(user.getKakaoId()).get();

        // then
        assertThat(findUser.getKakaoId()).isEqualTo(user.getKakaoId());
    }

    @Test
    @DisplayName("회원 ID로 회원 조회")
    void findById(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        User savedUser = userRepository.save(user);

        // when
        User findUser = userRepository.findById(savedUser.getId()).get();

        // then
        assertThat(findUser.getKakaoId()).isEqualTo(user.getKakaoId());
        assertThat(findUser.getNickName()).isEqualTo(user.getNickName());
        assertThat(findUser.getProfileImage()).isEqualTo(user.getProfileImage());
        assertThat(findUser.getThumbnailImage()).isEqualTo(user.getThumbnailImage());
        assertThat(findUser.getRole()).isEqualTo(user.getRole());
        assertThat(findUser.getStatus()).isEqualTo(user.getStatus());
    }

    @Test
    @DisplayName("회원 닉네임으로 조회")
    void findByNickName(){
        // given
        User user = User.createUser(1L,"황주환","image", "image","10~19","male", UserRole.USER);
        User savedUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByNickName(savedUser.getNickName()).get();

        // then
        assertThat(findUser.getId()).isEqualTo(savedUser.getId());
        assertThat(findUser.getNickName()).isEqualTo(savedUser.getNickName());
    }


}