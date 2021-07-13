package com.study.locationservice.service;

import com.study.locationservice.LocationFixture;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.locationservice.LocationFixture.*;
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
        // given
        List<LocationCreateRequest> request = new ArrayList<>();
        request.add(TEST_LOCATION_CREATE_REQUEST1);
        request.add(TEST_LOCATION_CREATE_REQUEST2);

        given(locationRepository.save(any()))
                .willReturn(null)
                .willReturn(null);

        // when
        locationService.create(request);

        // then
        then(locationRepository).should(times(2)).save(any());
    }

    @Test
    @DisplayName("회원 Id로 조회")
    void findById(){
        // given
        given(locationRepository.findById(any()))
                .willReturn(Optional.of(TEST_LOCATION));

        // when
        LocationResponse locationResponse = locationService.findById(1L);

        // then
        assertThat(locationResponse.getCode()).isEqualTo(TEST_LOCATION.getCode());
        assertThat(locationResponse.getCity()).isEqualTo(TEST_LOCATION.getCity());
        assertThat(locationResponse.getGu()).isEqualTo(TEST_LOCATION.getGu());
        assertThat(locationResponse.getDong()).isEqualTo(TEST_LOCATION.getDong());
        assertThat(locationResponse.getLen()).isEqualTo(TEST_LOCATION.getLen());
        assertThat(locationResponse.getLet()).isEqualTo(TEST_LOCATION.getLet());
        assertThat(locationResponse.getCodeType()).isEqualTo(TEST_LOCATION.getCodeType());
    }

    @Test
    @DisplayName("지역정보 검색어로 조회")
    void findBySearchName() {
        // given
        PageRequest pageable = PageRequest.of(0, 20);

        List<Location> content = new ArrayList<>();
        content.add(TEST_LOCATION);

        Page<Location> pageLocations = new PageImpl<>(content, pageable, content.size());

        given(locationQueryRepository.findBySearchCondition(any(),any()))
                .willReturn(pageLocations);

        // when
        Page<LocationResponse> result
                = locationService.findBySearchCondition(pageable,TEST_LOCATION_SEARCH_REQUEST );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getId()).isEqualTo(TEST_LOCATION.getId());
        assertThat(result.getContent().get(0).getCode()).isEqualTo(TEST_LOCATION.getCode());
        assertThat(result.getContent().get(0).getDong()).isEqualTo(TEST_LOCATION.getDong());
    }

    @Test
    @DisplayName("지역정보 코드로 조회")
    void findByCode(){
        // given
        given(locationRepository.findByCode(any()))
                .willReturn(Optional.of(TEST_LOCATION));

        // when
        LocationResponse result = locationService.findByCode(TEST_LOCATION_CODE_REQUEST);

        // then
        assertThat(result.getId()).isEqualTo(TEST_LOCATION.getId());
        assertThat(result.getCode()).isEqualTo(TEST_LOCATION.getCode());
        assertThat(result.getDong()).isEqualTo(TEST_LOCATION.getDong());
    }

    @Test
    @DisplayName("지역정보 ID와 검색거리로 주변 지역정보를 조회한다.")
    void findAroundByLocation(){
        // given
        given(locationRepository.findById(any()))
                .willReturn(Optional.of(TEST_LOCATION));

        given(locationQueryRepository.findAroundByLocation(any(),any()))
                .willReturn(Arrays.asList(TEST_LOCATION,TEST_LOCATION2));

        // when
        List<LocationResponse> result = locationService.findAroundById(1L, 3);

        // then
        assertThat(result.size()).isEqualTo(2);
        then(locationRepository).should(times(1)).findById(any());
        then(locationQueryRepository).should(times(1)).findAroundByLocation(any(),any());
    }



}
