package com.study.study.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.area.Area;
import com.study.study.dto.QStudyResponse;
import com.study.study.dto.StudyResponse;
import com.study.study.dto.StudySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.study.area.QArea.area;
import static com.study.study.QStudy.study;
import static com.study.study.QTag.tag;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<StudyResponse> findBySearchCondition(Area area, Integer distance, StudySearchRequest request) {
        return queryFactory
                .select(new QStudyResponse(study))
                .from(study)
                .leftJoin(study.tags, tag).fetchJoin()
                .where(nameLike(request.getSearchWord()),
                        categoryEq(request.getCategoryId()),
                        online(request.getOnline()),
                        offline(request.getOffline()),
                        studyIdLt(request.getStudyId()),
                        areaIdIn(area, distance))
                .orderBy(study.id.desc())
                .limit(request.getSize())
                .fetch();

    }

    private BooleanExpression studyIdLt(Long studyId) {
        return studyId != null ? study.id.lt(studyId) : null;
    }

    private BooleanExpression areaIdIn(Area userArea, Integer distance) {
        return userArea.getLen() != null ? study.area.id.in(
                JPAExpressions
                        .select(area.id)
                        .from(area)
                        .where(betweenLen(userArea.getLen(), distance),
                                betweenLet(userArea.getLet(), distance))) : null;
    }

    private BooleanExpression betweenLen(Double len, Integer searchDistance) {
        // 지구 반지름 6400km
        double earthRadius = 6400.0;

        // 도 = 거리 / (반지름 * 라디안)
        double degree = 1 / (earthRadius * (Math.PI / 180));
        return area.len.between(len - degree * searchDistance, len + degree * searchDistance);
    }

    private BooleanExpression betweenLet(Double let, Integer searchDistance) {
        // 지구 반지름 6400km
        double earthRadius = 6400.0;
        // 도 = 거리 / (반지름 * 라디안 * cos(위도))
        double degree = 1 / ((earthRadius * Math.cos(Math.toRadians(let))) * 1 * (Math.PI / 180));
        return area.let.between(let - degree * searchDistance, let + degree * searchDistance);
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
