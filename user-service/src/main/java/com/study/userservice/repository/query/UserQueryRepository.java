package com.study.userservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.userservice.domain.QInterestTag;
import com.study.userservice.domain.QStudyApply;
import com.study.userservice.domain.QUser;
import com.study.userservice.domain.User;
import com.study.userservice.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.userservice.domain.QInterestTag.*;
import static com.study.userservice.domain.QStudyApply.*;
import static com.study.userservice.domain.QUser.*;
import static com.study.userservice.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<User> findByIdIn(List<Long> userIdList){
        return queryFactory
                .selectFrom(user).distinct()
                .where(user.id.in(userIdList))
                .fetch();
    }

    public User findWithInterestTagById(Long userId){
        User findUser = queryFactory
                .selectFrom(user).distinct()
                .leftJoin(user.interestTags, interestTag).fetchJoin()
                .where(user.id.eq(userId))
                .fetchOne();

        if(findUser == null){
            throw new UserException(userId + "는 존재하지 않는 회원 ID 입니다.");
        }

        return findUser;
    }

    public User findWithStudyApplyById(Long userId){
        User findUser = queryFactory
                .selectFrom(user).distinct()
                .leftJoin(user.studyApplies, studyApply).fetchJoin()
                .where(user.id.eq(userId))
                .orderBy(studyApply.id.desc())
                .fetchOne();

        if(findUser == null){
            throw new UserException(userId + "는 존재하지 않는 회원 ID 입니다.");
        }

        return findUser;
    }

    public List<User> findWithStudyApplyByStudyId(Long studyId){
        return queryFactory
                .selectFrom(user).distinct()
                .leftJoin(user.studyApplies,studyApply).fetchJoin()
                .where(studyApply.studyId.eq(studyId))
                .orderBy(studyApply.id.desc())
                .fetch();
    }
}
