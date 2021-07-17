package com.study.gatheringservice.repository;

import com.study.gatheringservice.domain.Gathering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GatheringRepository extends JpaRepository<Gathering,Long> {

    @Query("select distinct g from Gathering g left join fetch g.gatheringUsers where g.id =:gatheringId")
    Optional<Gathering> findWithGatheringUsersById(@Param("gatheringId") Long gatheringId);

    Page<Gathering> findByStudyIdOrderByGatheringTimeDesc(Long studyId, Pageable pageable);

    @Query("select distinct g from Gathering g left join fetch g.gatheringUsers where g.studyId =:studyId")
    List<Gathering> findWithGatheringUsersByStudyId(@Param("studyId") Long studyId);
}
