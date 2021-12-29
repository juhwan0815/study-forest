package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.NotificationResponse;
import com.study.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<Slice<NotificationResponse>> findByUserId(@LoginUser Long userId,
                                                                    @PageableDefault(page = 0, size = 25) Pageable pageable) {
        return ResponseEntity.ok(notificationService.findByUserId(userId, pageable));
    }
}
