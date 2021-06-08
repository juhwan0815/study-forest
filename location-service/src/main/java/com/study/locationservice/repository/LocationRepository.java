package com.study.locationservice.repository;

import com.study.locationservice.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Long> {

    Optional<Location> findByCode(String code);
}
