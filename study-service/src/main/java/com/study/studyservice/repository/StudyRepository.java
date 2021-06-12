package com.study.studyservice.repository;

import com.study.studyservice.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study,Long> {
}
