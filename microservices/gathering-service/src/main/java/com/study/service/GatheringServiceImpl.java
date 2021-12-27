package com.study.service;

import com.study.client.UserResponse;
import com.study.client.UserServiceClient;
import com.study.domain.Gathering;
import com.study.domain.Place;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import com.study.kakfa.GatheringCreateMessage;
import com.study.kakfa.StudyDeleteMessage;
import com.study.kakfa.UserDeleteMessage;
import com.study.kakfa.sender.GatheringCreateMessageSender;
import com.study.repository.GatheringQueryRepository;
import com.study.repository.GatheringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringServiceImpl implements GatheringService {

    private final GatheringRepository gatheringRepository;
    private final GatheringQueryRepository gatheringQueryRepository;
    private final UserServiceClient userServiceClient;
    private final GatheringCreateMessageSender gatheringCreateMessageSender;

    @Override
    @Transactional
    public GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request) {

        Gathering gathering = Gathering.createGathering(studyId, request.getGatheringTime(), request.getOffline(), request.getContent());
        gathering.addGatheringUser(userId, true);

        if (request.getOffline()) {
            Place place = Place.createPlace(request.getPlaceName(), request.getLet(), request.getLen());
            gathering.changePlace(place);
        }

        gatheringRepository.save(gathering);
        gatheringCreateMessageSender.send(GatheringCreateMessage.from(gathering.getStudyId(), gathering.getGatheringTime(),
                request.getOffline(), gathering.getContent()));
        return GatheringResponse.from(gathering);
    }

    @Override
    public Page<GatheringResponse> findByStudyId(Long studyId, Pageable pageable) {
        Page<Gathering> gatherings = gatheringRepository.findByStudyIdOrderByIdDesc(studyId, pageable);
        return gatherings.map(gathering -> GatheringResponse.from(gathering));
    }

    @Override
    @Transactional
    public void deleteByStudyId(StudyDeleteMessage studyDeleteMessage) {
        List<Gathering> gatherings = gatheringRepository.findWithGatheringUserByStudyId(studyDeleteMessage.getStudyId());
        gatheringRepository.deleteAll(gatherings);
    }

    @Override
    @Transactional
    public GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request) {
        Gathering findGathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new RuntimeException());
        findGathering.isRegister(userId);

        findGathering.update(request.getGatheringTime(), request.getOffline(), request.getContent());

        if (request.getOffline()) {
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

    @Override
    public GatheringResponse findById(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUserById(gatheringId)
                .orElseThrow(() -> new RuntimeException());
        return GatheringResponse.from(findGathering);
    }

    @Override
    @Transactional
    public void addGatheringUser(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUserById(gatheringId)
                .orElseThrow(() -> new RuntimeException());

        findGathering.addGatheringUser(userId, false);
    }

    @Override
    @Transactional
    public void deleteGatheringUser(Long userId, Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUserById(gatheringId)
                .orElseThrow(() -> new RuntimeException());

        findGathering.deleteGatheringUser(userId);
    }

    @Override
    @Transactional
    public void deleteGatheringUser(UserDeleteMessage userDeleteMessage) {
        List<Gathering> gatherings = gatheringQueryRepository.findWithGatheringUserByUserId(userDeleteMessage.getUserId());
        gatherings.forEach(gathering -> gathering.deleteGatheringUser(userDeleteMessage.getUserId()));
    }

    @Override
    public List<UserResponse> findGatheringUserById(Long gatheringId) {
        Gathering findGathering = gatheringRepository.findWithGatheringUserById(gatheringId)
                .orElseThrow(() -> new RuntimeException());

        List<Long> userIds = findGathering.getGatheringUserId();
        return userServiceClient.findByIdIn(userIds);
    }

}
