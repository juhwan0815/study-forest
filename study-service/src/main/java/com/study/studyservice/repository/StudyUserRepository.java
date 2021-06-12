package com.study.studyservice.repository;

import com.study.studyservice.domain.Role;
import com.study.studyservice.domain.Study;
import com.study.studyservice.domain.StudyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyUserRepository extends JpaRepository<StudyUser,Long> {

    Optional<StudyUser> findByUserIdAndAndRoleAndStudy(Long userId, Role role, Study study);
}
