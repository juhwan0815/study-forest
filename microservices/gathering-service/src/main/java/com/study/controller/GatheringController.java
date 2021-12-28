package com.study.controller;

import com.study.client.UserResponse;
import com.study.config.LoginUser;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import com.study.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/studies/{studyId}/gatherings")
    public ResponseEntity<Page<GatheringResponse>> findByStudyId(@PathVariable Long studyId,
                                                                 @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return ResponseEntity.ok(gatheringService.findByStudyId(studyId, pageable));
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
    public ResponseEntity<GatheringResponse> findById(@PathVariable Long gatheringId) {
        return ResponseEntity.ok(gatheringService.findById(gatheringId));
    }

    @PostMapping("/gatherings/{gatheringId}/gatheringUsers")
    public ResponseEntity<Void> addGatheringUser(@LoginUser Long userId,
                                                 @PathVariable Long gatheringId) {
        gatheringService.addGatheringUser(userId, gatheringId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/gatherings/{gatheringId}/gatheringUsers")
    public ResponseEntity<Void> deleteGatheringUser(@LoginUser Long userId,
                                                    @PathVariable Long gatheringId) {
        gatheringService.deleteGatheringUser(userId, gatheringId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/gatherings/{gatheringId}/gatheringUsers")
    public ResponseEntity<List<UserResponse>> findGatheringUsersById(@PathVariable Long gatheringId) {
        return ResponseEntity.ok(gatheringService.findGatheringUserById(gatheringId));
    }

}
