package com.study.service;

import com.study.client.UserResponse;
import com.study.client.UserServiceClient;
import com.study.domain.Gathering;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import com.study.kakfa.StudyDeleteMessage;
import com.study.kakfa.UserDeleteMessage;
import com.study.kakfa.sender.GatheringCreateMessageSender;
import com.study.repository.GatheringQueryRepository;
import com.study.repository.GatheringRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.GatheringFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GatheringServiceTest {

    @InjectMocks
    private GatheringServiceImpl gatheringService;

    @Mock
    private GatheringRepository gatheringRepository;

    @Mock
    private GatheringQueryRepository gatheringQueryRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private GatheringCreateMessageSender gatheringCreateMessageSender;

    @Test
    @DisplayName("온라인 모임을 생성한다.")
    void createOnline() {
        // given
        GatheringCreateRequest gatheringCreateRequest
                = new GatheringCreateRequest(LocalDateTime.now(), false, "온라인 스터디", null, null, null);

        given(gatheringRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(gatheringCreateMessageSender)
                .send(any());
        // when
        GatheringResponse result = gatheringService.create(1L, 1L, gatheringCreateRequest);

        // then
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(gatheringCreateRequest.getContent());
        assertThat(result.getGatheringTime()).isEqualTo(gatheringCreateRequest.getGatheringTime());
        assertThat(result.getOffline()).isEqualTo(gatheringCreateRequest.getOffline());
        assertThat(result.getNumberOfPeople()).isEqualTo(1);
        assertThat(result.getPlace()).isNull();
        then(gatheringRepository).should(times(1)).save(any());
        then(gatheringCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("오프라인 모임을 생성한다.")
    void createOffline() {
        // given
        given(gatheringRepository.save(any()))
                .willReturn(null);

        willDoNothing()
                .given(gatheringCreateMessageSender)
                .send(any());
        // when
        GatheringResponse result = gatheringService.create(1L, 1L, TEST_GATHERING_CREATE_REQUEST);

        // then
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING_CREATE_REQUEST.getContent());
        assertThat(result.getGatheringTime()).isEqualTo(TEST_GATHERING_CREATE_REQUEST.getGatheringTime());
        assertThat(result.getOffline()).isEqualTo(TEST_GATHERING_CREATE_REQUEST.getOffline());
        assertThat(result.getNumberOfPeople()).isEqualTo(1);
        assertThat(result.getPlace().getName()).isEqualTo(TEST_GATHERING_CREATE_REQUEST.getPlaceName());
        assertThat(result.getPlace().getLet()).isEqualTo(TEST_GATHERING_CREATE_REQUEST.getLet());
        assertThat(result.getPlace().getLen()).isEqualTo(TEST_GATHERING_CREATE_REQUEST.getLen());
        then(gatheringRepository).should(times(1)).save(any());
        then(gatheringCreateMessageSender).should(times(1)).send(any());
    }

    @Test
    @DisplayName("온라인 스터디로 수정한다.")
    void updateOnline() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), true, "오프라인 모임");
        gathering.addGatheringUser(1L, true);

        GatheringUpdateRequest gatheringUpdateRequest
                = new GatheringUpdateRequest(LocalDateTime.now(), false, "온라인 스터디", null, null, null);

        given(gatheringRepository.findWithGatheringUserById(any()))
                .willReturn(Optional.of(gathering));

        // when
        GatheringResponse result = gatheringService.update(1L, 1L, gatheringUpdateRequest);

        // then
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(gatheringUpdateRequest.getContent());
        assertThat(result.getGatheringTime()).isEqualTo(gatheringUpdateRequest.getGatheringTime());
        assertThat(result.getOffline()).isEqualTo(gatheringUpdateRequest.getOffline());
        assertThat(result.getPlace()).isNull();
        then(gatheringRepository).should(times(1)).findWithGatheringUserById(any());
    }

    @Test
    @DisplayName("오프라인 스터디로 수정한다.")
    void updateOffline() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        gathering.addGatheringUser(1L, true);

        given(gatheringRepository.findWithGatheringUserById(any()))
                .willReturn(Optional.of(gathering));

        // when
        GatheringResponse result = gatheringService.update(1L, 1L, TEST_GATHERING_UPDATE_REQUEST);

        // then
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST.getContent());
        assertThat(result.getGatheringTime()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST.getGatheringTime());
        assertThat(result.getOffline()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST.getOffline());
        assertThat(result.getPlace().getName()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST.getPlaceName());
        assertThat(result.getPlace().getLet()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST.getLet());
        assertThat(result.getPlace().getLen()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST.getLen());
        then(gatheringRepository).should(times(1)).findWithGatheringUserById(any());
    }

    @Test
    @DisplayName("모임을 삭제한다.")
    void delete() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        gathering.addGatheringUser(1L, true);

        given(gatheringRepository.findWithGatheringUserById(any()))
                .willReturn(Optional.of(gathering));

        willDoNothing()
                .given(gatheringRepository)
                .delete(any());

        // when
        gatheringService.delete(1L, 1L);

        // then
        then(gatheringRepository).should(times(1)).findWithGatheringUserById(any());
        then(gatheringRepository).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("모임을 조회한다.")
    void findById() {
        // given
        given(gatheringRepository.findById(any()))
                .willReturn(Optional.of(TEST_GATHERING));

        // when
        GatheringResponse result = gatheringService.findById(1L);

        // then
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING.getContent());
        assertThat(result.getGatheringTime()).isEqualTo(TEST_GATHERING.getGatheringTime());
        assertThat(result.getStudyId()).isEqualTo(TEST_GATHERING.getStudyId());
        assertThat(result.getOffline()).isEqualTo(TEST_GATHERING.isOffline());
        then(gatheringRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("모임 참가자를 추가한다.")
    void addGatheringUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");

        given(gatheringRepository.findWithGatheringUserById(any()))
                .willReturn(Optional.of(gathering));

        // when
        gatheringService.addGatheringUser(1L, 1L);

        // then
        then(gatheringRepository).should(times(1)).findWithGatheringUserById(any());
    }

    @Test
    @DisplayName("모임 참가자를 삭제한다.")
    void deleteGatheringUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        gathering.addGatheringUser(1L, false);

        given(gatheringRepository.findWithGatheringUserById(any()))
                .willReturn(Optional.of(gathering));

        // when
        gatheringService.deleteGatheringUser(1L, 1L);

        // then
        then(gatheringRepository).should(times(1)).findWithGatheringUserById(any());
    }

    @Test
    @DisplayName("회원 탈퇴로 인한 모임 참가자를 삭제한다.")
    void deleteGatheringUserByDeleteUser() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        gathering.addGatheringUser(1L, false);

        given(gatheringQueryRepository.findWithGatheringUserByUserId(any()))
                .willReturn(Arrays.asList(gathering));

        // when
        gatheringService.deleteGatheringUser(UserDeleteMessage.from(1L));

        // then
        then(gatheringQueryRepository).should(times(1)).findWithGatheringUserByUserId(any());
    }

    @Test
    @DisplayName("모임 참가자 리스트를 조회한다.")
    void findGatheringUserById() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        gathering.addGatheringUser(1L, false);

        given(gatheringRepository.findWithGatheringUserById(any()))
                .willReturn(Optional.of(gathering));

        given(userServiceClient.findByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE));

        // when
        List<UserResponse> result = gatheringService.findGatheringUserById(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(gatheringRepository).should(times(1)).findWithGatheringUserById(any());
        then(userServiceClient).should(times(1)).findByIdIn(any());
    }

    @Test
    @DisplayName("스터디의 모임을 조회한다.")
    void findByStudyId() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        PageRequest pageable = PageRequest.of(0, 10);

        Slice<Gathering> gatherings = new SliceImpl<>(Arrays.asList(gathering), pageable, true);

        given(gatheringRepository.findByStudyIdOrderByIdDesc(any(), any()))
                .willReturn(gatherings);

        // when
        Slice<GatheringResponse> result = gatheringService.findByStudyId(1L, pageable);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.hasNext()).isEqualTo(true);
        then(gatheringRepository).should(times(1)).findByStudyIdOrderByIdDesc(any(), any());
    }

    @Test
    @DisplayName("스터디 삭제로 인한 모임 삭체 처리를 한다.")
    void deleteByStudyId() {
        // given
        Gathering gathering = Gathering.createGathering(1L, LocalDateTime.now(), false, "온라인 모임");
        given(gatheringRepository.findWithGatheringUserByStudyId(any()))
                .willReturn(Arrays.asList(gathering));

        willDoNothing()
                .given(gatheringRepository)
                .deleteAll(any());

        // when
        gatheringService.deleteByStudyId(StudyDeleteMessage.from(1L, Arrays.asList(1L)));

        // then
        then(gatheringRepository).should(times(1)).findWithGatheringUserByStudyId(any());
        then(gatheringRepository).should(times(1)).deleteAll(any());
    }
}