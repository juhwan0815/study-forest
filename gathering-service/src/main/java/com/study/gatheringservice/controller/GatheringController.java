package com.study.gatheringservice.controller;

import com.study.gatheringservice.config.LoginUser;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.exception.GatheringException;
import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gathering.GatheringUpdateRequest;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import com.study.gatheringservice.service.GatheringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyToOne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GatheringController {

    private final GatheringService gatheringService;

    @GetMapping("/studies/{studyId}/gatherings")
    public ResponseEntity<Page<GatheringResponse>> find(@PathVariable Long studyId,
                                                        @PageableDefault(size = 25,page = 0) Pageable pageable){
        return ResponseEntity.ok(gatheringService.find(studyId,pageable));
    }

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

    @DeleteMapping("/gatherings/{gatheringId}")
    public ResponseEntity<Void> delete(@LoginUser Long userId,
                                       @PathVariable Long gatheringId){
        gatheringService.delete(userId,gatheringId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/gatherings/{gatheringId}")
    public ResponseEntity<GatheringResponse> findById(@LoginUser Long userId,
                                                      @PathVariable Long gatheringId){
        return ResponseEntity.ok(gatheringService.findById(userId,gatheringId));
    }

    @PostMapping("/gatherings/{gatheringId}/users")
    public ResponseEntity<Void> addGatheringUser(@LoginUser Long userId,
                                                 @PathVariable Long gatheringId){
        gatheringService.addGatheringUser(userId,gatheringId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/gatherings/{gatheringId}/users")
    public ResponseEntity<List<GatheringUserResponse>> findGatheringUsers(@PathVariable Long gatheringId){
        return ResponseEntity.ok(gatheringService.findGatheringUsers(gatheringId));
    }

    @DeleteMapping("/gatherings/{gatheringId}/users")
    public ResponseEntity<Void> deleteGatheringUser(@LoginUser Long userId,
                                                    @PathVariable Long gatheringId){
        gatheringService.deleteGatheringUser(userId,gatheringId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
