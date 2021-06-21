package com.study.userservice.controller;

import com.study.userservice.config.LoginUser;
import com.study.userservice.model.interestTag.InterestTagResponse;
import com.study.userservice.model.studyapply.StudyApplyResponse;
import com.study.userservice.model.user.UserFindRequest;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserResponse;
import com.study.userservice.model.user.UserUpdateProfileRequest;
import com.study.userservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserLoginRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long userId){
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping("/users/profile")
    public ResponseEntity<UserResponse> findUserByLoginId(@LoginUser Long userId){
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> findUserByIdIn(UserFindRequest request){
        return ResponseEntity.ok(userService.findByIdIn(request));
    }

    @PatchMapping("/users/profile")
    public ResponseEntity<UserResponse> changeProfile(@LoginUser Long userId,
                                                      @RequestPart(required = false) MultipartFile image,
                                                      @RequestPart @Valid UserUpdateProfileRequest request){
        return ResponseEntity.ok(userService.updateProfile(userId,image,request));
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> delete(@LoginUser Long userId){
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/users/locations/{locationId}")
    public ResponseEntity<UserResponse> changeLocation(@LoginUser Long userId,@PathVariable Long locationId){
        return ResponseEntity.ok(userService.updateLocation(userId,locationId));
    }

    @PostMapping("/users/tags/{tagId}")
    public ResponseEntity<Void> addInterestTag(@LoginUser Long userId, @PathVariable Long tagId){
        userService.addInterestTag(userId,tagId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/users/tags/{tagId}")
    public ResponseEntity<Void> deleteInterestTag(@LoginUser Long userId,@PathVariable Long tagId){
        userService.deleteInterestTag(userId,tagId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/tags")
    public ResponseEntity<List<InterestTagResponse>> findInterestTagsByUserId(@LoginUser Long userId){
        return ResponseEntity.ok(userService.findInterestTagByUserId(userId));
    }

    @GetMapping("/users/studyApply")
    public ResponseEntity<List<StudyApplyResponse>> findStudyAppliesByUserId(@LoginUser Long userId){
        return ResponseEntity.ok(userService.findStudyAppliesByUserId(userId));
    }




}
