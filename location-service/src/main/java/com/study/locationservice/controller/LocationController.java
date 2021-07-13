package com.study.locationservice.controller;

import com.study.locationservice.domain.Location;
import com.study.locationservice.model.LocationCodeRequest;
import com.study.locationservice.model.LocationCreateRequest;
import com.study.locationservice.model.LocationResponse;
import com.study.locationservice.model.LocationSearchRequest;
import com.study.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/locations")
    public ResponseEntity<Void> create(@RequestBody List<LocationCreateRequest> request){
        locationService.create(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/locations/{locationId}")
    public ResponseEntity<LocationResponse> findById(@PathVariable Long locationId){
        return ResponseEntity.ok(locationService.findById(locationId));
    }

    @GetMapping("/locations/search")
    public ResponseEntity<Page<LocationResponse>> search(@PageableDefault(size = 20) Pageable pageable,
                                                         @Valid LocationSearchRequest request){
        return ResponseEntity.ok(locationService.findBySearchCondition(pageable,request));
    }

    @GetMapping("/locations/code")
    public ResponseEntity<LocationResponse> code(@Valid LocationCodeRequest request){
        return ResponseEntity.ok(locationService.findByCode(request));
    }

    @GetMapping("/locations/{locationId}/around")
    public ResponseEntity<List<LocationResponse>> around(@PathVariable Long locationId,
                                                 @RequestParam Integer searchDistance){
        return ResponseEntity.ok(locationService.findAroundById(locationId,searchDistance));
    }




}
