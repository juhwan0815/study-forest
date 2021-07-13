package com.study.locationservice.service;

import com.study.locationservice.model.LocationCodeRequest;
import com.study.locationservice.model.LocationCreateRequest;
import com.study.locationservice.model.LocationResponse;
import com.study.locationservice.model.LocationSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {

    void create(List<LocationCreateRequest> request);

    LocationResponse findById(Long locationId);

    Page<LocationResponse> findBySearchCondition(Pageable pageable, LocationSearchRequest request);

    LocationResponse findByCode(LocationCodeRequest request);

    List<LocationResponse> findAroundById(Long locationId,Integer searchDistance);
}
