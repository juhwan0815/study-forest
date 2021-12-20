package com.study.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.domain.Area;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AreaQueryRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private AreaRepository areaRepository;

    private AreaQueryRepository areaQueryRepository;

    @BeforeEach
    void init() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        areaQueryRepository = new AreaQueryRepository(queryFactory);
    }

    @Test
    @DisplayName("지역으로 주변 지역을 조회한다.")
    void findAroundByArea() {
        // given
        List<Area> areas = new ArrayList<>();
        areas.add(Area.createArea("2823752000", "인천광역시", "부평구", "부평2동",null,37.486874, 126.71794, "H"));
        areas.add(Area.createArea("2823753000", "인천광역시", "부평구", "부평3동",null,37.486111, 126.708309, "H"));
        areas.add(Area.createArea("2823754000", "인천광역시", "부평구", "부평4동",null,37.500906, 126.724893, "H"));
        areas.add(Area.createArea("2823755000", "인천광역시", "부평구", "부평5동",null,37.493609, 126.728399, "H"));

        areaRepository.saveAll(areas);

        em.flush();
        em.clear();

        // when
        List<Area> result = areaQueryRepository.findAroundByArea(areas.get(0), 3);

        // then
        assertThat(result.size()).isEqualTo(4);
    }


}