package com.study.locationservice.service;

import com.querydsl.core.QueryResults;
import com.study.locationservice.domain.Location;
import com.study.locationservice.model.LocationCodeRequest;
import com.study.locationservice.model.LocationCreateRequest;
import com.study.locationservice.model.LocationResponse;
import com.study.locationservice.model.LocationSearchRequest;
import com.study.locationservice.repository.LocationRepository;
import com.study.locationservice.repository.query.LocationQueryRepository;
import com.study.locationservice.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.study.locationservice.domain.QLocation.location;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationQueryRepository locationQueryRepository;

    @Test
    @DisplayName("지역정보(위치) 저장")
    void create(){
        LocationCreateRequest request1 = new LocationCreateRequest();
        request1.setCode("1111054000");
        request1.setCity("서울특별시");
        request1.setGu("종로구");
        request1.setDong("삼청동");
        request1.setLen(126.980996);
        request1.setLet(37.590758);
        request1.setCodeType("H");

        LocationCreateRequest request2 = new LocationCreateRequest();
        request1.setCode("1111051500");
        request1.setCity("서울특별시");
        request1.setGu("종로구");
        request1.setDong("청운효자동");
        request1.setLen(126.970626);
        request1.setLet(37.584009);
        request1.setCodeType("H");

        List<LocationCreateRequest> request = new ArrayList<>();
        request.add(request1);
        request.add(request2);

        given(locationRepository.save(any()))
                .willReturn(null)
                .willReturn(null);

        locationService.create(request);

        then(locationRepository).should(times(2)).save(any());
    }

    @Test
    @DisplayName("회원 Id로 조회")
    void findById(){
        // given
        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        given(locationRepository.findById(any()))
                .willReturn(Optional.of(location));

        // when
        LocationResponse locationResponse = locationService.findById(1L);

        // then
        assertThat(locationResponse.getCode()).isEqualTo(location.getCode());
        assertThat(locationResponse.getCity()).isEqualTo(location.getCity());
        assertThat(locationResponse.getGu()).isEqualTo(location.getGu());
        assertThat(locationResponse.getDong()).isEqualTo(location.getDong());
        assertThat(locationResponse.getLen()).isEqualTo(location.getLen());
        assertThat(locationResponse.getLet()).isEqualTo(location.getLet());
        assertThat(locationResponse.getCodeType()).isEqualTo(location.getCodeType());
    }

    @Test
    @DisplayName("지역정보 검색어로 조회")
    void findBySearchName() {
        // given
        LocationSearchRequest locationSearchRequest = new LocationSearchRequest();
        locationSearchRequest.setSearchName("삼청동");

        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");
        List<Location> content = new ArrayList<>();
        content.add(location);
        PageRequest pageable = PageRequest.of(0, 20);

        Page<Location> pageLocations = new PageImpl<>(content, pageable, content.size());

        given(locationQueryRepository.findBySearchCondition(pageable,locationSearchRequest))
                .willReturn(pageLocations);

        // when
        Page<LocationResponse> result = locationService.findBySearchCondition(pageable, locationSearchRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getDong()).isEqualTo(locationSearchRequest.getSearchName());
    }

    @Test
    @DisplayName("지역정보 코드로 조회")
    void findByCode(){
        LocationCodeRequest locationCodeRequest = new LocationCodeRequest();
        locationCodeRequest.setCode("1111051500");

        Location location = Location.createLocation("1111051500", "서울특별시", "종로구", "삼청동",
                null, 37.590758, 126.980996, "H");

        given(locationRepository.findByCode(any()))
                .willReturn(Optional.of(location));

        LocationResponse result = locationService.findByCode(locationCodeRequest);

        assertThat(result.getCode()).isEqualTo(locationCodeRequest.getCode());
        assertThat(result.getDong()).isEqualTo(location.getDong());
    }



}
