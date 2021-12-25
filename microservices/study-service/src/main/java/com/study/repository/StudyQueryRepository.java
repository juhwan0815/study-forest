package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.QStudy;
import com.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.study.domain.QCategory.category;
import static com.study.domain.QTag.tag;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Study findWithCategoryAndTagById(Long studyId){
        Study findStudy = queryFactory
                .selectFrom(QStudy.study).distinct()
                .leftJoin(QStudy.study.category, category).fetchJoin()
                .leftJoin(category.parent, category).fetchJoin()
                .leftJoin(QStudy.study.tags, tag).fetchJoin()
                .where(QStudy.study.id.eq(studyId))
                .fetchOne();
        if(findStudy == null){
            throw new RuntimeException();
        }
        return findStudy;
    }



}
