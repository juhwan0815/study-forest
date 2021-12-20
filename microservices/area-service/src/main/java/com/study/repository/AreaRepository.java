package com.study.repository;

import com.study.domain.Area;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Slice<Area> findByDongContainsOrRiContainsOrderById(String dong, String ri);
}
