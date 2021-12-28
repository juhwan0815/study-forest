package com.study.repository;

import com.study.domain.Gathering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GatheringRepositoryTest {

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        gatheringRepository.deleteAll();
    }

    @Test
    @DisplayName("모임과 모임 참가자를 같이 조회한다.")
    void findWithGatheringUserById() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        gatheringRepository.save(gathering);

        em.flush();
        em.clear();

        // when
        Gathering result = gatheringRepository.findWithGatheringUserById(gathering.getId()).get();

        // then
        assertThat(result.getGatheringUsers().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 ID로 모임 리스트를 조회한다.")
    void findByStudyIdOrderByIdDesc() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gatheringRepository.save(gathering);

        em.flush();
        em.clear();

        // when
        Page<Gathering> result = gatheringRepository.findByStudyIdOrderByIdDesc(1L, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 ID로 모임과 모임참가자를 조회한다.")
    void findWithGatheringUserByStudyId() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        gatheringRepository.save(gathering);

        em.flush();
        em.clear();

        // when
        List<Gathering> result = gatheringRepository.findWithGatheringUserByStudyId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getGatheringUsers().size()).isEqualTo(1);
    }
}