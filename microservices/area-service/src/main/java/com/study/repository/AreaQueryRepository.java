package com.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Area;
import com.study.dto.AreaResponse;
import com.study.dto.QAreaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.domain.QArea.area;

@Repository
@RequiredArgsConstructor
public class AreaQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<AreaResponse> findBySearchCondition(String searchWord, Long areaId, Integer size) {
        return queryFactory
                .select(new QAreaResponse(area))
                .from(area)
                .where(area.dong.contains(searchWord).or(area.ri.contains(searchWord)),
                        areaIdLt(areaId))
                .orderBy(area.id.desc())
                .limit(size)
                .fetch();

    }

    private BooleanExpression areaIdLt(Long areaId) {
        return areaId != null ? area.id.lt(areaId) : null;
    }

    public List<AreaResponse> findAroundByArea(Area findArea, Integer searchDistance) {
        return queryFactory
                .select(new QAreaResponse(area))
                .from(area)
                .where(betweenLen(findArea.getLen(), searchDistance),
                        betweenLet(findArea.getLet(), searchDistance))
                .fetch();
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
}
