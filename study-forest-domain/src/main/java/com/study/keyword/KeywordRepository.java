package com.study.keyword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("select k from Keyword k where k.user.id =:userId")
    List<Keyword> findByUserId(@Param("userId") Long userId);
}
