package com.study.userservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.userservice.domain.QUser;
import com.study.userservice.model.user.QUserResponse;
import com.study.userservice.model.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.userservice.domain.QUser.*;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<UserResponse> findByIdIn(List<Long> userIdList){
        return queryFactory
                .select(new QUserResponse(user.id,user.nickName))
                .from(user)
                .where(user.id.in(userIdList))
                .fetch();
    }

}
