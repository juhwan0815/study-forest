package com.study.gatheringservice.repository;

import com.study.gatheringservice.domain.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GatheringRepository extends JpaRepository<Gathering,Long> {

    @Query("select g from Gathering g left join fetch g.gatheringUsers where g.id =:gatheringId")
    Optional<Gathering> findWithGatheringUsersById(@Param("gatheringId") Long gatheringId);
}
