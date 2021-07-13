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

import java.util.List;

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

    @Test
    @DisplayName("주변 동네 검색 테스트")
    void findAroundByLocation(){
        // given
        Location location1 = Location.createLocation("2823751000", "인천광역시", "부평구", "부평1동",null,37.494168, 126.720032, "H");
        Location location2 = Location.createLocation("2823752000", "인천광역시", "부평구", "부평2동",null,37.486874, 126.71794, "H");
        Location location3 = Location.createLocation("2823753000", "인천광역시", "부평구", "부평3동",null,37.486111, 126.708309, "H");
        Location location4 = Location.createLocation("2823754000", "인천광역시", "부평구", "부평4동",null,37.500906, 126.724893, "H");
        Location location5 = Location.createLocation("2823755000", "인천광역시", "부평구", "부평5동",null,37.493609, 126.728399, "H");
        Location location6 = Location.createLocation("2823756000", "인천광역시", "부평구", "부평6동",null,37.486458, 126.724449, "H");
        Location location7 = Location.createLocation("2823757000", "인천광역시", "부평구", "산곡1동",null,37.506944, 126.700236, "H");
        Location location8 = Location.createLocation("2823758000", "인천광역시", "부평구", "산곡2동",null,37.505903, 126.708771, "H");
        Location location9 = Location.createLocation("2823758100", "인천광역시", "부평구", "산곡3동",null,37.490794, 126.709709, "H");
        Location location10 =Location.createLocation("2823758200", "인천광역시", "부평구", "산곡4동",null,37.501743, 126.711511, "H");

        locationRepository.save(location1);
        locationRepository.save(location2);
        locationRepository.save(location3);
        locationRepository.save(location4);
        locationRepository.save(location5);
        locationRepository.save(location6);
        locationRepository.save(location7);
        locationRepository.save(location8);
        locationRepository.save(location9);
        locationRepository.save(location10);

        em.flush();
        em.clear();

        // when
        List<Location> result = locationQueryRepository.findAroundByLocation(location10, 3);

        // then
        assertThat(result.size()).isEqualTo(10);
    }

}