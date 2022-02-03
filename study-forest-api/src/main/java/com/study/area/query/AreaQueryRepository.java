package com.study.area.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.area.dto.AreaResponse;
import com.study.area.dto.QAreaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.study.area.QArea.area;

@Repository
@RequiredArgsConstructor
public class AreaQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<AreaResponse> findBySearchCondition(Long areaId, String searchWord, Integer size) {
        return queryFactory
                .select(new QAreaResponse(area))
                .from(area)
                .where(dongLike(searchWord),
                        riLike(searchWord),
                        areaIdLt(areaId))
                .orderBy(area.id.desc())
                .limit(size)
                .fetch();

    }

    private BooleanExpression areaIdLt(Long areaId) {
        return areaId != null ? area.id.lt(areaId) : null;
    }

    private BooleanExpression dongLike(String searchWord) {
        return StringUtils.hasText(searchWord) ? area.dong.contains(searchWord) : null;
    }

    private BooleanExpression riLike(String searchWord) {
        return StringUtils.hasText(searchWord) ? area.ri.contains(searchWord) : null;
    }

}
