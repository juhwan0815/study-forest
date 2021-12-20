package com.study.controller;

import com.study.dto.AreaCreateRequest;
import com.study.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping("/areas")
    private ResponseEntity<Void> create(@RequestBody List<AreaCreateRequest> requests){
        areaService.create(requests);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
