package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.User;
import com.study.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserQueryRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private UserQueryRepository userQueryRepository;

    @BeforeEach
    void init() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        userQueryRepository = new UserQueryRepository(queryFactory);
    }

    @Test
    @DisplayName("회원 Id로 회원과 관심 키워드를 조회한다.")
    void findWithKeywordById() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        user.addKeyword("개발");
        user.addKeyword("토익");
        userRepository.save(user);

        em.flush();
        em.clear();

        // when
        User findUser = userQueryRepository.findWithKeywordById(user.getId());

        // then
        assertThat(findUser.getKeywords().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("예외 테스트 : 존재하지 않는 회원 Id로 회원과 관심 키워드를 조회하면 예외가 발생한다.")
    void findWithKeywordByNotExistId() {
        // given
        assertThrows(RuntimeException.class, () -> userQueryRepository.findWithKeywordById(1L));
    }

    @Test
    @DisplayName("검색 키워드가 포함되는 키워드를 가진 회원을 조회한다.")
    void findByKeywordContentContain(){
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", UserRole.USER);
        user.addKeyword("개발");
        user.addKeyword("토익");
        userRepository.save(user);

        em.flush();
        em.clear();

        // when
        List<User> result = userQueryRepository.findByKeywordContentContain("개발");

        // then
        assertThat(result.size()).isEqualTo(1);
    }

}