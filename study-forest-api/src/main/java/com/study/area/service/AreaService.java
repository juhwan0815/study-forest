package com.study.area.service;

import com.study.area.Area;
import com.study.area.AreaRepository;
import com.study.area.dto.AreaCreateRequest;
import com.study.area.dto.AreaResponse;
import com.study.area.dto.AreaSearchRequest;
import com.study.area.query.AreaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final AreaQueryRepository areaQueryRepository;

    @Transactional
    public void create(List<AreaCreateRequest> requests) {
        requests.forEach(request -> {
            Area area = Area.createArea(request.getCode(), request.getCity(), request.getGu(), request.getDong(),
                    request.getRi(), request.getLet(), request.getLen(), request.getCodeType());
            areaRepository.save(area);
        });
    }

    public List<AreaResponse> findByDongOrRi(AreaSearchRequest request) {
        return areaQueryRepository.findBySearchCondition(request.getAreaId(), request.getSearchWord(), request.getSize());
    }
}
