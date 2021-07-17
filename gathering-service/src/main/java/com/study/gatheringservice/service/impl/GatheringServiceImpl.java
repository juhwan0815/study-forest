package com.study.gatheringservice.service.impl;

import com.study.gatheringservice.client.StudyServiceClient;
import com.study.gatheringservice.client.UserServiceClient;
import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.GatheringUser;
import com.study.gatheringservice.domain.Place;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.exception.GatheringException;
import com.study.gatheringservice.kafka.message.StudyDeleteMessage;
import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gathering.GatheringUpdateRequest;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import com.study.gatheringservice.model.studyuser.StudyUserResponse;
import com.study.gatheringservice.model.user.UserResponse;
import com.study.gatheringservice.repository.GatheringRepository;
import com.study.gatheringservice.service.GatheringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringServiceImpl implements GatheringService {

    private final GatheringRepository gatheringRepository;
    private final StudyServiceClient studyServiceClient;
    private final UserServiceClient userServiceClient;

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

        Place place = CreatePlaceIfShapeIsOffline(request.getShape(), request.getPlaceName(),
                                                  request.getLet(),   request.getLen());

        gathering.changePlace(place);
        gathering.addGatheringUser(userId, true);

        Gathering savedGathering = gatheringRepository.save(gathering);
        return GatheringResponse.from(savedGathering);
    }

    @Override
    @Transactional
    public GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request) {
        Gathering findGathering = gatheringRepository.findWithGatheringUsersById(gatheringId)
                .orElseThrow(() -> new GatheringException(gatheringId + "는 존재하지 않는 모임 ID 입니다."));

        findGathering.checkRegister(userId);

        Place place = CreatePlaceIfShapeIsOffline(request.getShape(), request.getPlaceName(),
                                                  request.getLet(), request.getLen());

        findGathering.update(request.getGatheringTime(),request.getShape(),request.getContent());
        findGathering.changePlace(place);

        return GatheringResponse.from(findGathering);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUsersById(gatheringId)
                .orElseThrow(() -> new GatheringException(gatheringId + "는 존재하지 않는 모임 ID 입니다."));

        findGathering.checkRegister(userId);

        gatheringRepository.delete(findGathering);
    }

    @Override
    public GatheringResponse findById(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUsersById(gatheringId)
                .orElseThrow(() -> new GatheringException(gatheringId + "는 존재하지 않는 모임 ID 입니다."));
        return GatheringResponse.from(findGathering,userId);
    }

    @Override
    public Page<GatheringResponse> find(Long studyId, Pageable pageable) {
        return gatheringRepository.findByStudyIdOrderByGatheringTimeDesc(studyId,pageable)
                .map(gathering -> GatheringResponse.from(gathering));
    }

    @Override
    @Transactional
    public void addGatheringUser(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUsersById(gatheringId)
                .orElseThrow(() -> new GatheringException(gatheringId + "는 존재하지 않는 모임 ID 입니다."));

        findGathering.addGatheringUser(userId,false);
    }

    @Override
    @Transactional
    public void deleteGatheringUser(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUsersById(gatheringId)
                .orElseThrow(() -> new GatheringException(gatheringId + "는 존재하지 않는 모임 ID 입니다."));

        findGathering.deleteGatheringUser(userId);
    }

    @Override
    public List<GatheringUserResponse> findGatheringUsers(Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUsersById(gatheringId)
                .orElseThrow(() -> new GatheringException(gatheringId + "는 존재하지 않는 모임 ID 입니다."));

        List<Long> userIdList = findGathering.getGatheringUsers().stream()
                .map(gatheringUser -> gatheringUser.getUserId())
                .collect(Collectors.toList());

        List<UserResponse> users = userServiceClient.findUserByIdIn(userIdList);

        List<GatheringUserResponse> gatheringUserResponses = new ArrayList<>();
        findGathering.getGatheringUsers().stream()
                .forEach(gatheringUser -> {
                    for (UserResponse user : users) {
                        if(gatheringUser.getUserId().equals(user.getId())){
                            gatheringUserResponses.add(GatheringUserResponse.from(gatheringUser,user));
                            break;
                        }
                    }
                });


        return gatheringUserResponses;
    }

    @Override
    @Transactional
    public void deleteByStudyId(StudyDeleteMessage studyDeleteMessage) {
        List<Gathering> findGatherings = gatheringRepository.findWithGatheringUsersByStudyId(studyDeleteMessage.getStudyId());
        gatheringRepository.deleteAll(findGatherings);
    }

    private Place CreatePlaceIfShapeIsOffline(Shape shape, String placeName, Double let, Double len) {
        Place place = null;
        if (shape.equals(Shape.OFFLINE)) {
            place = Place.createPlace(placeName, let, len);
        }
        return place;
    }


}
