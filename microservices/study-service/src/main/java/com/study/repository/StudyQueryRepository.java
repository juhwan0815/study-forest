package com.study.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.*;
import com.study.dto.study.StudySearchRequest;
import com.study.exception.ChatRoomNotFoundException;
import com.study.exception.StudyNotFoundException;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.study.domain.QCategory.category;
import static com.study.domain.QChatRoom.*;
import static com.study.domain.QStudy.*;
import static com.study.domain.QStudyUser.*;
import static com.study.domain.QTag.tag;
import static com.study.domain.QWaitUser.*;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Study findByChatRoomId(Long chatRoomId) {
        Study findStudy = queryFactory
                .selectFrom(study)
                .join(study.chatRooms, chatRoom)
                .where(chatRoom.id.eq(chatRoomId))
                .fetchOne();

        if (findStudy == null) {
            throw new ChatRoomNotFoundException(chatRoomId + "는 존재하지 않는 채팅방 ID 입니다.");
        }
        return findStudy;
    }

    public List<Study> findWithWaitUserByUserId(Long userId) {
        return queryFactory
                .selectFrom(study).distinct()
                .join(study.waitUsers, waitUser).fetchJoin()
                .where(waitUser.userId.eq(userId))
                .fetch();
    }

    public List<Study> findWithStudyUsersByUserId(Long userId) {
        return queryFactory
                .selectFrom(study).distinct()
                .join(study.studyUsers, studyUser).fetchJoin()
                .where(studyUser.userId.eq(userId))
                .fetch();
    }

    public Study findWithCategoryAndTagById(Long studyId) {
        Study findStudy = queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.category, category).fetchJoin()
                .leftJoin(category.parent, category).fetchJoin()
                .leftJoin(study.tags, tag).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();
        if (findStudy == null) {
            throw new StudyNotFoundException(studyId + "는 존재하지 않는 스터디 ID 입니다.");
        }
        return findStudy;
    }

    public List<Study> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.studyUsers, studyUser)
                .leftJoin(study.tags, tag).fetchJoin()
                .where(studyUser.userId.eq(userId))
                .fetch();
    }

    public Page<Study> findBySearchCondition(StudySearchRequest request, List<Long> areaIds,
                                             Pageable pageable) {
        QueryResults<Study> result = queryFactory
                .selectFrom(study).distinct()
                .where(nameLike(request.getKeyword()),
                        areaIn(areaIds),
                        categoryEq(request.getCategoryId()),
                        online(request.getOnline()),
                        offline(request.getOffline()))
                .orderBy(study.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    private BooleanExpression areaIn(List<Long> areaIds) {
        return areaIds != null ? study.areaId.in(areaIds) : null;
    }

    private BooleanExpression nameLike(String searchKeyword) {
        return StringUtils.hasText(searchKeyword) ? study.name.contains(searchKeyword) : null;
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId != null ? study.category.id.eq(categoryId) : null;
    }

    private BooleanExpression online(Boolean online) {
        return online != null ? study.online.eq(online) : null;
    }

    private BooleanExpression offline(Boolean offline) {
        return offline != null ? study.offline.eq(offline) : null;
    }

}
