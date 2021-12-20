package com.study.repository;

import com.study.domain.Area;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AreaRepositoryTest {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("지역코드로 지역을 조회한다.")
    void findByCode(){
        // given
        Area area = Area.createArea("1111051500", "서울특별시", "종로구", "삼청동", null, 37.590758, 126.980996, "H");
        areaRepository.save(area);

        em.flush();
        em.clear();

        // when
        Area result = areaRepository.findByCode(area.getCode()).get();

        // then
        assertThat(result.getId()).isEqualTo(area.getId());
    }

    @Test
    @DisplayName("동/리 검색어로 지역을 조회한다.")
    void findByDongOrRi() {
        // given
        Area area = Area.createArea("1111051500", "서울특별시", "종로구", "삼청동", null, 37.590758, 126.980996, "H");
        areaRepository.save(area);

        em.flush();
        em.clear();

        // when
        Slice<Area> result = areaRepository.findByDongContainsOrRiContainsOrderById("삼청동","삼청동");

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
    }




}