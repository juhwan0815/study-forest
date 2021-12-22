package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.QKeyword;
import com.study.domain.QUser;
import com.study.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.study.domain.QKeyword.*;
import static com.study.domain.QUser.*;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public User findWithKeywordById(Long userId) {
        User findUser = queryFactory
                .selectFrom(user).distinct()
                .leftJoin(user.keywords, keyword).fetchJoin()
                .where(user.id.eq(userId))
                .fetchOne();

        if (findUser == null) {
            throw new RuntimeException();
        }
        return findUser;
    }

}
