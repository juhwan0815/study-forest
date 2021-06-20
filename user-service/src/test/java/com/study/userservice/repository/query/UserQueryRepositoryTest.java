package com.study.userservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.repository.UserRepository;
import jdk.jfr.Unsigned;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserQueryRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    private JPAQueryFactory queryFactory;

    private UserQueryRepository userQueryRepository;

    @BeforeEach
    void setUp(){
        queryFactory = new JPAQueryFactory(em);
        userQueryRepository = new UserQueryRepository(queryFactory);
    }

    @Test
    @DisplayName("회원 ID 리스트로 회원 목록을 조회한다.")
    void findByIdIn(){
        // given
        User user1 = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        User user2 = User.createUser(2L, "황철원", "10~19", "male", UserRole.USER);
        userRepository.save(user1);
        userRepository.save(user2);

        em.flush();
        em.clear();

        // when
        List<User> result = userQueryRepository.findByIdIn(Arrays.asList(1L, 2L));

        // then
        assertThat(result.size()).isEqualTo(2);
    }
}