package com.study.locationservice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.locationservice.domain.Location;
import com.study.locationservice.domain.QLocation;
import com.study.locationservice.model.LocationSearchRequest;
import com.study.locationservice.repository.query.LocationQueryRepository;
import org.apache.catalina.realm.JAASCallbackHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;

import java.util.List;

import static com.study.locationservice.domain.QLocation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("지역정보(위치) 저장")
    void create(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        // when
        Location savedLocation = locationRepository.save(location);

        // then
        assertThat(savedLocation.getId()).isNotNull();
    }

    @Test
    @DisplayName("지역정보 ID로 조회테스트")
    void findById(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        Location savedLocation = locationRepository.save(location);

        // when
        Location result = locationRepository.findById(savedLocation.getId()).get();

        // then
        assertThat(result.getId()).isEqualTo(savedLocation.getId());
        assertThat(result.getDong()).isEqualTo(savedLocation.getDong());
    }


    @Test
    @DisplayName("코드로 조회 테스트")
    void findByCode(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        Location savedLocation = locationRepository.save(location);

        // when
        Location findLocation = locationRepository.findByCode(location.getCode()).get();

        // then
        assertThat(findLocation.getId()).isEqualTo(savedLocation.getId());
        assertThat(findLocation.getCode()).isEqualTo(savedLocation.getCode());
    }

    @Test
    @DisplayName("검색어로 조회 테스트")
    void findBySearchName(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        Location savedLocation = locationRepository.save(location);

        LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
        locationSearchRequest.setSearchName("삼청동");
        PageRequest pageable = PageRequest.of(0, 20);

        // when
        Page<Location> result = locationQueryRepository.findBySearchCondition(pageable, locationSearchRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCode()).isEqualTo(location.getCode());
        assertThat(result.getContent().get(0).getDong()).isEqualTo(locationSearchRequest.getSearchName());
    }


}