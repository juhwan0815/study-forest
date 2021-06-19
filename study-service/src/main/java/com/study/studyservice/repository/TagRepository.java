package com.study.studyservice.repository;


import com.study.studyservice.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {

    List<Tag> findByNameIn(List<String> tagNames);


}
