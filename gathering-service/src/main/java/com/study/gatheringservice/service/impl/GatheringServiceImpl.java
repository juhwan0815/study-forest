package com.study.gatheringservice.service.impl;

import com.study.gatheringservice.client.StudyServiceClient;
import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.Place;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.exception.GatheringException;
import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.studyuser.StudyUserResponse;
import com.study.gatheringservice.repository.GatheringRepository;
import com.study.gatheringservice.service.GatheringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringServiceImpl implements GatheringService {

    private final GatheringRepository gatheringRepository;
    private final StudyServiceClient studyServiceClient;

    @Override
    @Transactional
    public GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request) {

        List<StudyUserResponse> studyUsers = studyServiceClient.findStudyUserByStudyId(studyId);

        studyUsers.stream()
                .filter(studyUser -> studyUser.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new GatheringException("스터디에 가입되지 않는 회원입니다."));

        Gathering gathering = Gathering.createGathering(studyId, request.getGatheringTime(),
                request.getShape(), request.getContent());

        Place place = CreatePlaceIfOffline(request);

        gathering.changePlace(place);
        gathering.addGatheringUser(userId, true);

        Gathering savedGathering = gatheringRepository.save(gathering);
        return GatheringResponse.from(savedGathering);
    }

    private Place CreatePlaceIfOffline(GatheringCreateRequest request) {
        Place place = null;
        if (request.getShape().equals(Shape.OFFLINE)) {
            place = Place.createPlace(request.getPlaceName(), request.getLet(), request.getLen());
        }
        return place;
    }


}
