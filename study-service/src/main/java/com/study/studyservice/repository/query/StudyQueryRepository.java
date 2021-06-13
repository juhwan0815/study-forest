package com.study.studyservice.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.studyservice.domain.QStudy;
import com.study.studyservice.domain.QStudyUser;
import com.study.studyservice.domain.Study;
import com.study.studyservice.domain.StudyUser;
import com.study.studyservice.exception.StudyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.studyservice.domain.QCategory.category;
import static com.study.studyservice.domain.QStudy.study;
import static com.study.studyservice.domain.QStudyTag.studyTag;
import static com.study.studyservice.domain.QTag.tag;

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
                .selectFrom(QStudy.study)
                .leftJoin(QStudy.study.studyUsers, QStudyUser.studyUser).fetchJoin()
                .where(QStudy.study.id.eq(studyId))
                .fetchOne();

        if(findStudy == null){
            throw new StudyException(studyId + "는 존재하지 않는 스터디 ID입니다.");
        }

        return findStudy;
    }
}
