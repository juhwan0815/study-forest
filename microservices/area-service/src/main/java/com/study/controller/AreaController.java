package com.study.controller;

import com.study.dto.*;
import com.study.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping("/areas")
    public ResponseEntity<Void> create(@RequestBody @Valid List<AreaCreateRequest> requests) {
        areaService.create(requests);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/areas/{areaId}")
    public ResponseEntity<AreaResponse> findById(@PathVariable Long areaId) {
        return ResponseEntity.ok(areaService.findById(areaId));
    }

    @GetMapping("/areas")
    public ResponseEntity<List<AreaResponse>> findBySearchRequest(@Valid AreaSearchRequest request) {
        return ResponseEntity.ok(areaService.findBySearchRequest(request));
    }

    @GetMapping("/areas/code")
    public ResponseEntity<AreaResponse> findByCode(@Valid AreaCodeRequest request) {
        return ResponseEntity.ok(areaService.findByCode(request));
    }

    @GetMapping("/areas/{areaId}/around")
    public ResponseEntity<List<AreaResponse>> findAroundById(@PathVariable Long areaId,
                                                             @Valid AreaAroundRequest request) {
        return ResponseEntity.ok(areaService.findAroundById(areaId, request));
    }
}
