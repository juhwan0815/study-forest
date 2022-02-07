package com.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dto.study.QStudyResponse;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.study.domain.QStudy.study;
import static com.study.domain.QStudyUser.studyUser;
import static com.study.domain.QTag.tag;
import static com.study.domain.QWaitUser.waitUser;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<StudyResponse> findByWaitUserId(Long userId) {
        return queryFactory
                .select(new QStudyResponse(study)).distinct()
                .from(study)
                .join(study.waitUsers, waitUser)
                .leftJoin(study.tags, tag).fetchJoin()
                .where(waitUser.userId.eq(userId))
                .fetch();
    }

    public List<StudyResponse> findByStudyUserId(Long userId) {
        return queryFactory
                .select(new QStudyResponse(study)).distinct()
                .from(study)
                .join(study.studyUsers, studyUser)
                .leftJoin(study.tags, tag).fetchJoin()
                .where(studyUser.userId.eq(userId))
                .fetch();
    }

    public List<StudyResponse> findBySearchCondition(StudySearchRequest request, List<Long> areaIds) {
        return queryFactory
                .select(new QStudyResponse(study)).distinct()
                .from(study)
                .leftJoin(study.tags, tag).fetchJoin()
                .where(nameLike(request.getKeyword()),
                        areaIn(areaIds),
                        categoryEq(request.getCategoryId()),
                        online(request.getOnline()),
                        offline(request.getOffline()),
                        studyIdLt(request.getStudyId()))
                .orderBy(study.id.desc())
                .limit(request.getSize())
                .fetch();
    }

    private BooleanExpression studyIdLt(Long studyId) {
        return studyId != null ? study.id.lt(studyId) : null;
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
