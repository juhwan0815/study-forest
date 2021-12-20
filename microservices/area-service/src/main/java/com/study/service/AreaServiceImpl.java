package com.study.service;

import com.study.domain.Area;
import com.study.dto.AreaCreateRequest;
import com.study.dto.AreaResponse;
import com.study.repository.AreaQueryRepository;
import com.study.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaServiceImpl implements AreaService{

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
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException(""));
        return AreaResponse.from(area);
    }


}
