package com.study.locationservice.service.impl;

import com.study.locationservice.domain.Location;
import com.study.locationservice.exception.LocationException;
import com.study.locationservice.model.LocationCodeRequest;
import com.study.locationservice.model.LocationCreateRequest;
import com.study.locationservice.model.LocationResponse;
import com.study.locationservice.model.LocationSearchRequest;
import com.study.locationservice.repository.LocationRepository;
import com.study.locationservice.repository.query.LocationQueryRepository;
import com.study.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationQueryRepository locationQueryRepository;

    @Override
    @Transactional
    public void create(List<LocationCreateRequest> request) {

        request.forEach(locationCreateRequest -> {
            Location location = Location.createLocation(locationCreateRequest.getCode(),
                    locationCreateRequest.getCity(),
                    locationCreateRequest.getGu(),
                    locationCreateRequest.getDong(),
                    locationCreateRequest.getRi(),
                    locationCreateRequest.getLet(),
                    locationCreateRequest.getLen(),
                    locationCreateRequest.getCodeType()
            );
            locationRepository.save(location);
        });
    }

    @Override
    public LocationResponse findById(Long locationId) {

        Location findLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationException(locationId + "는 존재하지 않는 지역정보ID 입니다."));

        return LocationResponse.from(findLocation);
    }

    @Override
    public Page<LocationResponse> findBySearchCondition(Pageable pageable,LocationSearchRequest request) {
        Page<Location> locations = locationQueryRepository.findBySearchCondition(pageable, request);
        return locations.map(location -> LocationResponse.from(location));
    }

    @Override
    public LocationResponse findByCode(LocationCodeRequest request) {
        Location findLocation = locationRepository.findByCode(request.getCode())
                .orElseThrow(() -> new LocationException(request.getCode() + "는 존재하지 않는 지역정보 코드입니다."));

        return LocationResponse.from(findLocation);
    }

    @Override
    public List<LocationResponse> findAroundById(Long locationId, Integer searchDistance) {
        Location findLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationException(locationId + "는 존재하지 않는 지역정보 ID입니다."));

        List<Location> aroundLocations = locationQueryRepository.findAroundByLocation(findLocation, searchDistance);

        return aroundLocations.stream()
                .map(location-> LocationResponse.from(location))
                .collect(Collectors.toList());
    }
}
