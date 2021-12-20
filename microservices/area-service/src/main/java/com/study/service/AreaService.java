package com.study.service;

import com.study.dto.AreaCodeRequest;
import com.study.dto.AreaCreateRequest;
import com.study.dto.AreaResponse;
import com.study.dto.AreaSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface AreaService {

    void create(List<AreaCreateRequest> requests);

    AreaResponse findById(Long areaId);

    Slice<AreaResponse> findByDongOrRi(AreaSearchRequest request, Pageable pageable);

    AreaResponse findByCode(AreaCodeRequest request);
}
