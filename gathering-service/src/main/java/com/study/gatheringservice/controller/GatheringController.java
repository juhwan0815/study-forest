package com.study.gatheringservice.controller;

import com.study.gatheringservice.config.LoginUser;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.exception.GatheringException;
import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gathering.GatheringUpdateRequest;
import com.study.gatheringservice.service.GatheringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping("/studies/{studyId}/gatherings")
    public ResponseEntity<GatheringResponse> create(@LoginUser Long userId,
                                                    @PathVariable Long studyId,
                                                    @RequestBody @Valid GatheringCreateRequest request){
        if(request.getShape() == Shape.OFFLINE){
            if(request.getPlaceName() == null ||!StringUtils.hasText(request.getPlaceName())){
                throw new GatheringException("오프라인일 경우 장소는 필수 값입니다.");
            }
            if(request.getLen() == null || request.getLet() == null){
                throw new GatheringException("오프라인일 경우 좌표는 필수 값입니다.");
            }
        }
        return ResponseEntity.ok(gatheringService.create(userId,studyId,request));
    }

    @PatchMapping("/gatherings/{gatheringId}")
    public ResponseEntity<GatheringResponse> update(@LoginUser Long userId,
                                                    @PathVariable Long gatheringId,
                                                    @RequestBody @Valid GatheringUpdateRequest request){
        if(request.getShape() == Shape.OFFLINE){
            if(request.getPlaceName() == null ||!StringUtils.hasText(request.getPlaceName())){
                throw new GatheringException("오프라인일 경우 장소는 필수 값입니다.");
            }
            if(request.getLen() == null || request.getLet() == null){
                throw new GatheringException("오프라인일 경우 좌표는 필수 값입니다.");
            }
        }
        return ResponseEntity.ok(gatheringService.update(userId,gatheringId,request));
    }

}
