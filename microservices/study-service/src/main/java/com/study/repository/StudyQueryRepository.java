package com.study.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.*;
import com.study.dto.study.StudySearchRequest;
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
import static com.study.domain.QStudy.*;
import static com.study.domain.QStudyUser.*;
import static com.study.domain.QTag.tag;
import static com.study.domain.QWaitUser.*;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Study> findWithWaitUserByUserId(Long userId){
        return queryFactory
                .selectFrom(study).distinct()
                .join(study.waitUsers, waitUser)
                .where(waitUser.userId.eq(userId))
                .orderBy(study.id.desc())
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
            throw new RuntimeException();
        }
        return findStudy;
    }

    public Page<Study> findBySearchCondition(StudySearchRequest request, List<Long> areaIds,
                                             Pageable pageable) {
        QueryResults<Study> result = queryFactory
                .selectFrom(study).distinct()
                .where(nameLike(request.getKeyword()),
                        areaIn(areaIds),
                        categoryEq(request.getCategoryId()),
                        online(request.getOnline()).or(offline(request.getOffline())))
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

    public List<Study> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.studyUsers, studyUser)
                .leftJoin(study.tags, tag).fetchJoin()
                .where(studyUser.userId.eq(userId))
                .fetch();
    }
}
