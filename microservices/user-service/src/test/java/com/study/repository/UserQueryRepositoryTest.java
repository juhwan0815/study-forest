package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("검색 키워드가 포함되는 키워드를 가진 회원을 조회한다.")
    void findByKeywordContentContain() {
        // given
        User user = User.createUser(1L, "황주환", "10~19", "male", "imageUrl", UserRole.USER);
        user.addKeyword("개발");
        user.addKeyword("토익");
        userRepository.save(user);

        // when
        List<UserResponse> result = userQueryRepository.findByKeywordContentContain("개발");

        // then
        assertThat(result.size()).isEqualTo(1);
    }

}