package com.study.locationservice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.locationservice.LocationFixture;
import com.study.locationservice.domain.Location;
import com.study.locationservice.model.LocationSearchRequest;
import com.study.locationservice.repository.query.LocationQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;

import static com.study.locationservice.LocationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EntityManager em;

    private JPAQueryFactory queryFactory;

    private LocationQueryRepository locationQueryRepository;

    @BeforeEach
    void init(){
        queryFactory = new JPAQueryFactory(em);
        locationQueryRepository = new LocationQueryRepository(queryFactory);
    }

    @Test
    @DisplayName("지역정보 ID로 조회테스트")
    void findById(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        Location savedLocation = locationRepository.save(location);

        em.flush();
        em.clear();

        // when
        Location result = locationRepository.findById(savedLocation.getId()).get();

        // then
        assertThat(result.getId()).isEqualTo(savedLocation.getId());
        assertThat(result.getDong()).isEqualTo(location.getDong());
    }


    @Test
    @DisplayName("코드로 조회 테스트")
    void findByCode(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        Location savedLocation = locationRepository.save(location);

        em.flush();
        em.clear();

        // when
        Location result = locationRepository.findByCode("1111051500").get();

        // then
        assertThat(result.getId()).isEqualTo(savedLocation.getId());
        assertThat(result.getCode()).isEqualTo("1111051500");
    }

    @Test
    @DisplayName("검색어로 조회 테스트")
    void findBySearchName(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");
        locationRepository.save(location);

        PageRequest pageable = PageRequest.of(0, 20);

        em.flush();
        em.clear();

        // when
        Page<Location> result = locationQueryRepository.findBySearchCondition(pageable, TEST_LOCATION_SEARCH_REQUEST);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo(location.getCode());
        assertThat(result.getContent().get(0).getDong()).isEqualTo(TEST_LOCATION_SEARCH_REQUEST.getSearchName());
    }


}