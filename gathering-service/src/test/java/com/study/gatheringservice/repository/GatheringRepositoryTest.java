package com.study.gatheringservice.repository;

import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.Shape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GatheringRepositoryTest {

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("모임과 모임 참가 인원을 같이 조회한다.")
    void findWithGatheringUsersById(){
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), Shape.ONLINE, "테스트 모임");
        gathering.addGatheringUser(1L,true);
        gathering.addGatheringUser(2L,false);
        gatheringRepository.save(gathering);

        em.flush();
        em.clear();

        // when
        Gathering result = gatheringRepository.findWithGatheringUsersById(gathering.getId()).get();

        // then
        assertThat(result.getId()).isEqualTo(gathering.getId());
        assertThat(result.getGatheringUsers().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("모임 날짜를 최신순으로 정렬하여 페이징 조회한다.")
    void findByStudyIdOrderByGatheringTimeDesc(){
        // given
        IntStream.range(0,10).forEach(value -> {
            LocalDateTime now = LocalDateTime.of(2021, 7, 17, 0 + value, 0);
            Gathering gathering = Gathering.createGathering(1L, now, Shape.ONLINE, "테스트 모임");
            gatheringRepository.save(gathering);
        });

        em.flush();
        em.clear();

        // when
        Page<Gathering> result = gatheringRepository.findByStudyIdOrderByGatheringTimeDesc(1L, PageRequest.of(0, 5));

        // then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(10);
    }
}