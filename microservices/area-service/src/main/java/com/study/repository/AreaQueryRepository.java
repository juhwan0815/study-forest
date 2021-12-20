package com.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Area;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.domain.QArea.area;

@Repository
@RequiredArgsConstructor
public class AreaQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Area> findAroundByArea(Area findArea, Integer searchDistance) {
        return queryFactory
                .selectFrom(area)
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
