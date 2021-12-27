package com.study.service;

import com.study.domain.Gathering;
import com.study.domain.Place;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import com.study.repository.GatheringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringServiceImpl implements GatheringService {

    private final GatheringRepository gatheringRepository;

    @Override
    @Transactional
    public GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request) {

        Gathering gathering = Gathering.createGathering(studyId, request.getGatheringTime(), request.getStatus(), request.getContent());
        gathering.addGatheringUser(userId, true);

        if (request.getStatus()) {
            Place place = Place.createPlace(request.getPlaceName(), request.getLet(), request.getLen());
            gathering.changePlace(place);
        }

        gatheringRepository.save(gathering);
        return GatheringResponse.from(gathering);
    }

    @Override
    @Transactional
    public GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request) {
        Gathering findGathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new RuntimeException());
        findGathering.isRegister(userId);

        findGathering.update(request.getGatheringTime(), request.getStatus(), request.getContent());

        if (request.getStatus()) {
            Place place = Place.createPlace(request.getPlaceName(), request.getLet(), request.getLen());
            findGathering.changePlace(place);
        }

        return GatheringResponse.from(findGathering);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new RuntimeException());
        findGathering.isRegister(userId);

        gatheringRepository.delete(findGathering);
    }


}
