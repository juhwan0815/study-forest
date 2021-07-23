package com.study.notificationservice.repository;

import com.study.notificationservice.domain.Notification;
import org.apache.kafka.streams.kstream.internals.InternalStreamsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import scala.collection.LongStepper;

import javax.persistence.EntityManager;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class NotificationRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("회원의 알림을 페이징 조회한다.")
    void findByUserIdOrderByCreatedAtDesc(){
        // given
        LongStream.range(0,10).forEach(value -> {
            Notification notification = Notification.createNotification(1L, "테스트 제목" + value, "테스트 내용" + value);
            notificationRepository.save(notification);
        });

        em.flush();
        em.clear();

        // when
        Page<Notification> result = notificationRepository.findByUserIdOrderByCreatedAtDesc(PageRequest.of(0, 3), 1L);

        // then
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(4);
    }

}