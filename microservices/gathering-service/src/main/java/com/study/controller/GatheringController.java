package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping("/studies/{studyId}/gatherings")
    public ResponseEntity<GatheringResponse> create(@LoginUser Long userId,
                                                    @PathVariable Long studyId,
                                                    @RequestBody @Valid GatheringCreateRequest request){
        return ResponseEntity.ok(gatheringService.create(userId, studyId, request));
    }

}
