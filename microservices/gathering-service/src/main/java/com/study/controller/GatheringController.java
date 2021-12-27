package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import com.study.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                                                    @RequestBody @Valid GatheringCreateRequest request) {
        return ResponseEntity.ok(gatheringService.create(userId, studyId, request));
    }

    @PatchMapping("/gatherings/{gatheringId}")
    public ResponseEntity<GatheringResponse> update(@LoginUser Long userId,
                                                    @PathVariable Long gatheringId,
                                                    @RequestBody @Valid GatheringUpdateRequest request) {
        return ResponseEntity.ok(gatheringService.update(userId, gatheringId, request));
    }

    @DeleteMapping("/gatherings/{gatheringId}")
    public ResponseEntity<GatheringResponse> delete(@LoginUser Long userId,
                                                    @PathVariable Long gatheringId) {
        gatheringService.delete(userId, gatheringId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/gatherings/{gatheringId}")
    public ResponseEntity<GatheringResponse> findById(@LoginUser Long userId,
                                                      @PathVariable Long gatheringId){
        return ResponseEntity.ok(gatheringService.findById(userId,gatheringId));
    }


}
