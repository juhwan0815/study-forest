package com.study.service;

import com.study.dto.AreaCreateRequest;
import com.study.dto.AreaResponse;

import java.util.List;

public interface AreaService {

    void create(List<AreaCreateRequest> requests);

    AreaResponse findById(Long areaId);
}
