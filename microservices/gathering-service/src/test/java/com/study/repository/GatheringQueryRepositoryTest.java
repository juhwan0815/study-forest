package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Gathering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GatheringQueryRepositoryTest {

    private GatheringQueryRepository gatheringQueryRepository;

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        gatheringQueryRepository = new GatheringQueryRepository(queryFactory);
        gatheringRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 ID로 모임과 모임 참가자를 같이 조회한다.")
    void findWithGatheringUserByUserId() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        gatheringRepository.save(gathering);

        em.flush();
        em.clear();

        // when
        List<Gathering> result = gatheringQueryRepository.findWithGatheringUserByUserId(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getGatheringUsers().size()).isEqualTo(1);
    }
}