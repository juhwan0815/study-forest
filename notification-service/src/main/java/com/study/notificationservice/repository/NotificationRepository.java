package com.study.notificationservice.repository;

import com.study.notificationservice.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Pageable pageable,Long userId);
}
