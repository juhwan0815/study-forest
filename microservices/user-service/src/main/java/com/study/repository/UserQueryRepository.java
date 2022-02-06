package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dto.user.QUserResponse;
import com.study.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.domain.QKeyword.keyword;
import static com.study.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<UserResponse> findByKeywordContentContain(String content) {
        return queryFactory
                .select(new QUserResponse(user)).distinct()
                .from(user)
                .join(user.keywords, keyword)
                .where(keyword.content.contains(content))
                .fetch();
    }

}
