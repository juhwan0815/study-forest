package com.study.controller;

import com.study.domain.Area;
import com.study.dto.AreaCodeRequest;
import com.study.dto.AreaCreateRequest;
import com.study.dto.AreaResponse;
import com.study.dto.AreaSearchRequest;
import com.study.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private ResponseEntity<Void> create(@RequestBody List<AreaCreateRequest> requests) {
        areaService.create(requests);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/areas/{areaId}")
    public ResponseEntity<AreaResponse> findById(@PathVariable Long areaId) {
        return ResponseEntity.ok(areaService.findById(areaId));
    }

    @GetMapping("/areas")
    public ResponseEntity<Slice<AreaResponse>> findByDongOrRi(@Valid AreaSearchRequest request,
                                                              @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(areaService.findByDongOrRi(request, pageable));
    }

    @GetMapping("/areas/code")
    public ResponseEntity<AreaResponse> findByCode(@Valid AreaCodeRequest request) {
        return ResponseEntity.ok(areaService.findByCode(request));
    }




}
