package com.study.locationservice.repository.query;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.locationservice.domain.Location;
import com.study.locationservice.model.LocationSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.locationservice.domain.QLocation.location;

@Repository
@RequiredArgsConstructor
public class LocationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Location> findBySearchCondition(Pageable pageable,
                                                LocationSearchRequest locationSearchRequest) {
        QueryResults<Location> result = queryFactory
                .selectFrom(location)
                .where(dongLike(locationSearchRequest.getSearchName())
                        .or(riLike(locationSearchRequest.getSearchName())))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchResults();

        List<Location> content = result.getResults();
        long total = result.getTotal();
        return new PageImpl<>(content,pageable,total);
    }

    private BooleanExpression dongLike(String searchName) {
        String substring = searchName.substring(searchName.length() - 1);
        if (substring.equals("동") || substring.equals("가")) {
            searchName = searchName.substring(0, searchName.length() - 1);
        }
        return location.dong.contains(searchName);
    }

    private BooleanExpression riLike(String searchName) {
        return searchName != null ?  location.ri.contains(searchName) : null;
    }

}
