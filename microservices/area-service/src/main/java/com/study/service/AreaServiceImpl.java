package com.study.service;

import com.study.domain.Area;
import com.study.dto.*;
import com.study.exception.AreaNotFoundException;
import com.study.repository.AreaQueryRepository;
import com.study.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final AreaQueryRepository areaQueryRepository;

    @Override
    @Transactional
    public void create(List<AreaCreateRequest> requests) {

        requests.forEach(request -> {
            Area area = Area.createArea(request.getCode(), request.getCity(), request.getGu(), request.getDong(),
                    request.getRi(), request.getLet(), request.getLen(), request.getCodeType());
            areaRepository.save(area);
        });
    }

    @Override
    public AreaResponse findById(Long areaId) {
        Area findArea = areaRepository.findById(areaId)
                .orElseThrow(() -> new AreaNotFoundException(areaId + "는 존재하지 않는 지역 ID 입니다."));
        return AreaResponse.from(findArea);
    }

    @Override
    public Slice<AreaResponse> findByDongOrRi(AreaSearchRequest request, Pageable pageable) {
        Slice<Area> areas = areaRepository.findByDongContainsOrRiContainsOrderById(request.getSearchWord(), request.getSearchWord());
        return areas.map(area -> AreaResponse.from(area));
    }

    @Override
    public AreaResponse findByCode(AreaCodeRequest request) {
        Area findArea = areaRepository.findByCode(request.getCode())
                .orElseThrow(() -> new AreaNotFoundException(request.getCode() + "는 존재하지 않는 지역 코드 입니다."));
        return AreaResponse.from(findArea);
    }

    @Override
    public List<AreaResponse> findAroundById(Long areaId, AreaAroundRequest request) {
        Area findArea = areaRepository.findById(areaId)
                .orElseThrow(() -> new AreaNotFoundException(areaId + "는 존재하지 않는 지역 ID 입니다."));
        List<Area> aroundAreas = areaQueryRepository.findAroundByArea(findArea, request.getDistance());
        return aroundAreas.stream()
                .map(area -> AreaResponse.from(area))
                .collect(Collectors.toList());
    }


}
