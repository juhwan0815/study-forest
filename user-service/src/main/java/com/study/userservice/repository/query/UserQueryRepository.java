package com.study.userservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.userservice.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

}
