package com.study.service;

import com.study.domain.Notification;
import com.study.dto.NotificationResponse;
import com.study.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Slice<NotificationResponse> findByUserId(Long userId, Pageable pageable) {
        Slice<Notification> notifications = notificationRepository.findByUserIdOrOrderById(pageable, userId);
        return notifications.map(notification -> NotificationResponse.from(notification));
    }
}
