package com.study.studyservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.studyservice.domain.QStudy;
import com.study.studyservice.domain.QWaitUser;
import com.study.studyservice.domain.Study;
import com.study.studyservice.exception.StudyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

    public Study findWithStudyTagsById(Long studyId){
        Study findStudy = queryFactory
                .selectFrom(study)
                .leftJoin(study.studyTags, studyTag).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();

        if(findStudy == null){
           throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public Study findWithStudyUsersById(Long studyId){
        Study findStudy = queryFactory
                .selectFrom(study)
                .leftJoin(study.studyUsers, studyUser).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();

        if(findStudy == null){
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public Study findWithCategoryAndStudyTagsAndTagById(Long studyId){
        Study findStudy = queryFactory
                .selectFrom(study)
                .leftJoin(study.category, category).fetchJoin()
                .leftJoin(category.parent, category).fetchJoin()
                .leftJoin(study.studyTags, studyTag).fetchJoin()
                .leftJoin(studyTag.tag, tag).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();

        if(findStudy == null){
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }

    public Study findWithWaitUserById(Long studyId){
        Study findStudy = queryFactory
                .selectFrom(study)
                .leftJoin(study.waitUsers, waitUser).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne();

        if(findStudy == null){
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }
}
