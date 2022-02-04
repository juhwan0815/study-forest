package com.study.studyuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyUserRepository extends JpaRepository<StudyUser, Long> {

    @Query("select su from StudyUser su where su.user.id=:userId and su.study.id=:studyId and su.studyRole=:studyRole")
    Optional<StudyUser> findByUserIdAndAndAndStudyIdAndStudyRole(@Param("userId") Long userId,
                                                                 @Param("studyId") Long studyId,
                                                                 @Param("StudyRole") StudyRole studyRole);
}
