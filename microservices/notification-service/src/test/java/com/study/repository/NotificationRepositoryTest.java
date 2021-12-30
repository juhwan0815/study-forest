package com.study.repository;

import com.study.domain.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class NotificationRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("회원의 알림을 조회한다.")
    void findByUserIdOrOrderById() {
        // given
        Notification notification = Notification.createNotification(1L, "테스트 제목", "테스트 내용");
        notificationRepository.save(notification);

        em.flush();
        em.clear();

        // when
        Slice<Notification> result = notificationRepository.findByUserIdOrderById(PageRequest.of(0, 10), 1L);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
    }
}