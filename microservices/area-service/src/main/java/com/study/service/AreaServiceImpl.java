package com.study.service;

import com.study.domain.Area;
import com.study.dto.*;
import com.study.exception.AreaNotFoundException;
import com.study.exception.NotFoundException;
import com.study.repository.AreaQueryRepository;
import com.study.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.exception.NotFoundException.*;

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
                .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));
        return AreaResponse.from(findArea);
    }

    @Override
    public List<AreaResponse> findBySearchRequest(AreaSearchRequest request) {
        return areaQueryRepository.findBySearchCondition(request.getSearchWord(), request.getAreaId(), request.getSize());
    }

    @Override
    public AreaResponse findByCode(AreaCodeRequest request) {
        Area findArea = areaRepository.findByCode(request.getCode())
                .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));
        return AreaResponse.from(findArea);
    }

    @Override
    public List<AreaResponse> findAroundById(Long areaId, AreaAroundRequest request) {
        Area findArea = areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));
        return areaQueryRepository.findAroundByArea(findArea, request.getDistance());
    }

}
