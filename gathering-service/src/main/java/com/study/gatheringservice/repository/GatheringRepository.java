package com.study.gatheringservice.repository;

import com.study.gatheringservice.domain.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<Gathering,Long> {
}
