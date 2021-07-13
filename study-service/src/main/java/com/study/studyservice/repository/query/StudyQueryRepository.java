package com.study.studyservice.repository.query;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.studyservice.domain.QStudy;
import com.study.studyservice.domain.QWaitUser;
import com.study.studyservice.domain.Study;
import com.study.studyservice.exception.StudyException;
import com.study.studyservice.model.study.request.StudySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.study.studyservice.domain.QCategory.category;
import static com.study.studyservice.domain.QStudy.*;
import static com.study.studyservice.domain.QStudy.study;
import static com.study.studyservice.domain.QStudyTag.studyTag;
import static com.study.studyservice.domain.QStudyUser.studyUser;
import static com.study.studyservice.domain.QTag.tag;
import static com.study.studyservice.domain.QWaitUser.*;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Study> findByUser(Long userId) {
        return queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.studyUsers, studyUser)
                .leftJoin(study.studyTags,studyTag).fetchJoin()
                .leftJoin(studyTag.tag,tag).fetchJoin()
                .where(studyUser.userId.eq(userId))
                .fetch();
    }

    public Study findWithStudyTagsById(Long studyId) {
        Study findStudy = queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.studyTags, studyTag).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();

        if (findStudy == null) {
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public Study findWithStudyUsersById(Long studyId) {
        Study findStudy = queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.studyUsers, studyUser).fetchJoin()
                .where(study.id.eq(studyId))
                .orderBy(studyUser.id.asc())
                .fetchOne();

        if (findStudy == null) {
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public Study findWithCategoryAndStudyTagsAndTagById(Long studyId) {
        Study findStudy = queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.category, category).fetchJoin()
                .leftJoin(category.parent, category).fetchJoin()
                .leftJoin(study.studyTags, studyTag).fetchJoin()
                .leftJoin(studyTag.tag, tag).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();

        if (findStudy == null) {
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public Study findWithWaitUserById(Long studyId) {
        Study findStudy = queryFactory
                .selectFrom(study).distinct()
                .leftJoin(study.waitUsers, waitUser).fetchJoin()
                .where(study.id.eq(studyId))
                .orderBy(waitUser.id.asc())
                .fetchOne();

        if (findStudy == null) {
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public List<Study> findByIdIn(List<Long> studyIdList) {
        return queryFactory
                .selectFrom(study).distinct()
                .where(study.id.in(studyIdList))
                .fetch();
    }

    public Page<Study> findBySearchCondition(StudySearchRequest request, List<Long> locationIdList,
                                             Pageable pageable) {
        QueryResults<Study> result = queryFactory
                .selectFrom(study).distinct()
                .where(nameLike(request.getSearchKeyword()),
                        locationIn(locationIdList),
                        categoryEq(request.getCategoryId()),
                        online(request.getOnline()).or(offline(request.getOffline())))
                .orderBy(study.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        List<Study> content = result.getResults();
        long total = result.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression locationIn(List<Long> locationIdList) {
        return locationIdList != null ? study.locationId.in(locationIdList) : null;
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
