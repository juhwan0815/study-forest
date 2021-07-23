package com.study.notificationservice.controller;

import com.study.notificationservice.config.LoginUser;
import com.study.notificationservice.model.notification.NotificationResponse;
import com.study.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationResponse>> findByUserId(@LoginUser Long userId,
                                                                   @PageableDefault(page = 0, size = 25) Pageable pageable) {
        return ResponseEntity.ok(notificationService.findByUserId(userId, pageable));
    }

}