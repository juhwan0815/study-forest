package com.study.service;

import com.study.dto.AreaCreateRequest;

import java.util.List;

public interface AreaService {

    void create(List<AreaCreateRequest> requests);
}
