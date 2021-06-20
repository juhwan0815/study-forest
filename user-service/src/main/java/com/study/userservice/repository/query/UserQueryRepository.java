package com.study.userservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.userservice.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.userservice.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<User> findByIdIn(List<Long> userIdList){
        return queryFactory
                .selectFrom(user)
                .where(user.id.in(userIdList))
                .fetch();
    }

}
