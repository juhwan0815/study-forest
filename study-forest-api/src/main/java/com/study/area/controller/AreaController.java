package com.study.area.controller;

import com.study.area.dto.AreaCreateRequest;
import com.study.area.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping("/api/areas")
    public ResponseEntity<Void> create(@RequestBody @Valid List<AreaCreateRequest> requests) {
        areaService.create(requests);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
