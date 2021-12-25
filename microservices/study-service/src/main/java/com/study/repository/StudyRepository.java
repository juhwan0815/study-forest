package com.study.repository;

import com.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select distinct s from Study s left join fetch s.studyUsers where s.id =:studyId")
    Optional<Study> findWithStudyUserById(@Param("studyId") Long studyId);

    @Query("select distinct s from Study s left join fetch s.waitUsers where s.id =:studyId")
    Optional<Study> findWithWaitUserById(@Param("studyId") Long studyId);

    @Query("select distinct s from Study s left join fetch s.chatRooms where s.id =:studyId")
    Optional<Study> findWithChatRoomById(@Param("studyId") Long studyId);
}
