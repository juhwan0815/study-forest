package com.study.service;

import com.study.AreaFixture;
import com.study.domain.Area;
import com.study.dto.AreaCreateRequest;
import com.study.dto.AreaResponse;
import com.study.repository.AreaQueryRepository;
import com.study.repository.AreaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.AreaFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @InjectMocks
    private AreaServiceImpl areaService;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private AreaQueryRepository areaQueryRepository;

    @Test
    @DisplayName("지역 리스트를 저장한다.")
    void createArea() {
        // given
        given(areaRepository.save(any()))
                .willReturn(null);

        // when
        areaService.create(Arrays.asList(TEST_AREA_CREATE_REQUEST));

        // then
        then(areaRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("지역 ID로 지역을 조회한다.")
    void findById() {
        // given
        given(areaRepository.findById(any()))
                .willReturn(Optional.of(TEST_AREA));

        // when
        AreaResponse result = areaService.findById(1L);

        // then
        assertThat(result.getCode()).isEqualTo(TEST_AREA.getCode());
        assertThat(result.getCity()).isEqualTo(TEST_AREA.getCity());
        assertThat(result.getGu()).isEqualTo(TEST_AREA.getGu());
        assertThat(result.getDong()).isEqualTo(TEST_AREA.getDong());
        assertThat(result.getRi()).isEqualTo(TEST_AREA.getRi());
        assertThat(result.getLen()).isEqualTo(TEST_AREA.getLen());
        assertThat(result.getLet()).isEqualTo(TEST_AREA.getLet());
        assertThat(result.getCodeType()).isEqualTo(TEST_AREA.getCodeType());

        then(areaRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("검색어로 지역을 조회한다.")
    void findBySearchWord() {
        // given
        List<Area> content = Arrays.asList(TEST_AREA);
        PageRequest pageable = PageRequest.of(0, 50);
        Slice<Area> sliceAreas = new SliceImpl<>(content, pageable, true);

        given(areaRepository.findByDongContainsOrRiContainsOrderById(any(), any()))
                .willReturn(sliceAreas);

        // when
        Slice<AreaResponse> result = areaService.findByDongOrRi(TEST_AREA_SEARCH_REQUEST, pageable);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();

        then(areaRepository).should(times(1)).findByDongContainsOrRiContainsOrderById(any(), any());
    }

    @Test
    @DisplayName("지역 코드로 지역을 조회한다.")
    void findByCode() {
        // given
        given(areaRepository.findByCode(any()))
                .willReturn(Optional.of(TEST_AREA));

        // when
        AreaResponse result = areaService.findByCode(TEST_AREA_CODE_REQUEST);

        // then
        assertThat(result.getCode()).isEqualTo(TEST_AREA.getCode());
        assertThat(result.getCity()).isEqualTo(TEST_AREA.getCity());
        assertThat(result.getGu()).isEqualTo(TEST_AREA.getGu());
        assertThat(result.getDong()).isEqualTo(TEST_AREA.getDong());
        assertThat(result.getRi()).isEqualTo(TEST_AREA.getRi());
        assertThat(result.getLen()).isEqualTo(TEST_AREA.getLen());
        assertThat(result.getLet()).isEqualTo(TEST_AREA.getLet());
        assertThat(result.getCodeType()).isEqualTo(TEST_AREA.getCodeType());

        then(areaRepository).should(times(1)).findByCode(any());
    }

    @Test
    @DisplayName("지역 ID와 검색거리로 주변 지역정보를 조회한다.")
    void findAroundById(){
        // given
        given(areaRepository.findById(any()))
                .willReturn(Optional.of(TEST_AREA));

        given(areaQueryRepository.findAroundByArea(any(),any()))
                .willReturn(Arrays.asList(TEST_AREA));

        // when
        List<AreaResponse> result = areaService.findAroundById(1L, TEST_AREA_AROUND_REQUEST);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(areaRepository).should(times(1)).findById(any());
        then(areaQueryRepository).should(times(1)).findAroundByArea(any(), any());
    }



}