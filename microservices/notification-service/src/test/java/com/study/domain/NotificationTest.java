package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    @DisplayName("알림을 생성한다.")
    void createNotification() {
        // given
        Long userId = 1L;
        String title = "스프링 스터디";
        String content = "스프링 스터디";

        // when
        Notification result = Notification.createNotification(userId, title, content);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getContent()).isEqualTo(content);
    }
}