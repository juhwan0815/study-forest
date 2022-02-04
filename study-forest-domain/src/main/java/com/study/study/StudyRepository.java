package com.study.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select distinct s from Study s left join fetch s.tags where s.id =:studyId")
    Optional<Study> findWithTagById(@Param("studyId") Long studyId);
}
