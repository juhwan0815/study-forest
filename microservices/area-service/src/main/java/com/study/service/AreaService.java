package com.study.service;

import com.study.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface AreaService {

    void create(List<AreaCreateRequest> requests);

    AreaResponse findById(Long areaId);

    Slice<AreaResponse> findByDongOrRi(AreaSearchRequest request, Pageable pageable);

    AreaResponse findByCode(AreaCodeRequest request);

    List<AreaResponse> findAroundById(Long areaId, AreaAroundRequest request);
}
