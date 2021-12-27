package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Gathering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.domain.QGathering.gathering;
import static com.study.domain.QGatheringUser.gatheringUser;

@Repository
@RequiredArgsConstructor
public class GatheringQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Gathering> findWithGatheringUserByUserId(Long userId) {
        return queryFactory
                .selectFrom(gathering)
                .join(gathering.gatheringUsers, gatheringUser).fetchJoin()
                .where(gatheringUser.userId.eq(userId))
                .fetch();
    }
}
