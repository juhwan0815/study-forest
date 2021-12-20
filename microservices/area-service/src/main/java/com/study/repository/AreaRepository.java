package com.study.repository;

import com.study.domain.Area;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Slice<Area> findByDongContainsOrRiContainsOrderById(String dong, String ri);

    Optional<Area> findByCode(String code);
}
