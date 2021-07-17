package com.study.gatheringservice.service;

import com.study.gatheringservice.GatheringFixture;
import com.study.gatheringservice.client.StudyServiceClient;
import com.study.gatheringservice.client.UserServiceClient;
import com.study.gatheringservice.domain.Gathering;
import com.study.gatheringservice.domain.Shape;
import com.study.gatheringservice.exception.GatheringException;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import com.study.gatheringservice.model.studyuser.StudyUserResponse;
import com.study.gatheringservice.repository.GatheringRepository;
import com.study.gatheringservice.service.impl.GatheringServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.gatheringservice.GatheringFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class GatheringServiceTest {

    @InjectMocks
    private GatheringServiceImpl gatheringService;

    @Mock
    private GatheringRepository gatheringRepository;

    @Mock
    private StudyServiceClient studyServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    @DisplayName("온라인 모임을 생성한다.")
    void createIfOnlineStudy(){
        // given
        List<StudyUserResponse> studyUsers = Arrays.asList(TEST_STUDY_USER_RESPONSE1, TEST_STUDY_USER_RESPONSE2);

        given(studyServiceClient.findStudyUserByStudyId(any()))
            .willReturn(studyUsers);

        given(gatheringRepository.save(any()))
                .willReturn(createOnlineGathering());

        // when
        GatheringResponse result = gatheringService.create(1L, 1L, TEST_GATHERING_CREATE_REQUEST1);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getGatheringTime()).isEqualTo(TEST_LOCAL_DATE_TIME1);
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING_CREATE_REQUEST1.getContent());
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getShape()).isEqualTo(Shape.ONLINE);
        assertThat(result.getNumberOfPeople()).isEqualTo(1);
        assertThat(result.getPlace()).isNull();

        then(studyServiceClient).should(times(1)).findStudyUserByStudyId(any());
        then(gatheringRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("오프라인 모임을 생성한다.")
    void createIfOfflineStudy(){
        // given
        List<StudyUserResponse> studyUsers = Arrays.asList(TEST_STUDY_USER_RESPONSE1, TEST_STUDY_USER_RESPONSE2);

        given(studyServiceClient.findStudyUserByStudyId(any()))
                .willReturn(studyUsers);

        given(gatheringRepository.save(any()))
                .willReturn(createOfflineGathering());

        // when
        GatheringResponse result = gatheringService.create(1L, 1L, TEST_GATHERING_CREATE_REQUEST2);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getGatheringTime()).isEqualTo(TEST_LOCAL_DATE_TIME1);
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING_CREATE_REQUEST2.getContent());
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getShape()).isEqualTo(Shape.OFFLINE);
        assertThat(result.getNumberOfPeople()).isEqualTo(1);
        assertThat(result.getPlace().getName()).isEqualTo(TEST_GATHERING_CREATE_REQUEST2.getPlaceName());
        assertThat(result.getPlace().getLen()).isEqualTo(TEST_GATHERING_CREATE_REQUEST2.getLen());
        assertThat(result.getPlace().getLet()).isEqualTo(TEST_GATHERING_CREATE_REQUEST2.getLet());

        then(studyServiceClient).should(times(1)).findStudyUserByStudyId(any());
        then(gatheringRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외 테스트 : 스터디 참가 인원이 아닌 회원이 모임을 생성할 경우 예외가 발생한다.")
    void createWhenLoginUserIsNotStudyUser(){
        // given
        List<StudyUserResponse> studyUsers = Arrays.asList(TEST_STUDY_USER_RESPONSE1, TEST_STUDY_USER_RESPONSE2);

        given(studyServiceClient.findStudyUserByStudyId(any()))
                .willReturn(studyUsers);

        // when
        assertThrows(GatheringException.class,
                ()->gatheringService.create(3L, 1L, TEST_GATHERING_CREATE_REQUEST2));
    }

    @Test
    @DisplayName("오프라인 모임에서 온라임 모임으로 수정한다.")
    void updateOfflineToOnline(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOfflineGathering()));

        // when
        GatheringResponse result = gatheringService.update(1L, 1L, TEST_GATHERING_UPDATE_REQUEST1);

        // then
        assertThat(result.getGatheringTime()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST1.getGatheringTime());
        assertThat(result.getShape()).isEqualTo(Shape.ONLINE);
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST1.getContent());
        assertThat(result.getPlace()).isNull();

        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }

    @Test
    @DisplayName("온라인 모임에서 오프라인 모임을 수정한다.")
    void updateOnlineToOffline(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        // when
        GatheringResponse result = gatheringService.update(1L, 1L, TEST_GATHERING_UPDATE_REQUEST2);

        // then
        assertThat(result.getGatheringTime()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST2.getGatheringTime());
        assertThat(result.getShape()).isEqualTo(Shape.OFFLINE);
        assertThat(result.getContent()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST2.getContent());
        assertThat(result.getPlace().getName()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST2.getPlaceName());
        assertThat(result.getPlace().getLen()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST2.getLen());
        assertThat(result.getPlace().getLet()).isEqualTo(TEST_GATHERING_UPDATE_REQUEST2.getLet());

        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }

    @Test
    @DisplayName("예외 테스트 : 모임 등록자가 아닌 유저가 모임을 수정할 경우 예외가 발생한다.")
    void updateWhenIsNotRegister(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        // when
        assertThrows(GatheringException.class,()->gatheringService.update(2L, 1L, TEST_GATHERING_UPDATE_REQUEST2));
    }

    @Test
    @DisplayName("모임 등록자가 모임을 삭제한다.")
    void delete(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        willDoNothing()
                .given(gatheringRepository)
                .delete(any());

        // when
        gatheringService.delete(1L,1L);

        // then
        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
        then(gatheringRepository).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("예외 테스트 : 모임 등록자가 아닌 유저가 모임을 삭제하려고 하면 예외가 발생한다.")
    void deleteWhenIsNotRegister(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        // when
        assertThrows(GatheringException.class,()->gatheringService.delete(2L,1L));
    }

    @Test
    @DisplayName("모임 참가자가 아닌 사람이 모임을 상세 조회한다")
    void findByIdWhenLoginUserIsNotGatheringUser(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        // when
        GatheringResponse result = gatheringService.findById(3L, 1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getGatheringTime()).isEqualTo(TEST_LOCAL_DATE_TIME1);
        assertThat(result.getShape()).isEqualTo(Shape.ONLINE);
        assertThat(result.getContent()).isEqualTo("테스트 모임");
        assertThat(result.getNumberOfPeople()).isEqualTo(1);
        assertThat(result.getPlace()).isNull();
        assertThat(result.getApply()).isEqualTo(false);

        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }

    @Test
    @DisplayName("모임 참가자갸 모임을 상세조회한다.")
    void findByIdWhenLoginUserIsGatheringUser(){
        // given
        Gathering testGathering = createOnlineGathering();
        testGathering.addGatheringUser(2L,false);

        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(testGathering));

        // when
        GatheringResponse result = gatheringService.findById(2L, 1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getGatheringTime()).isEqualTo(TEST_LOCAL_DATE_TIME1);
        assertThat(result.getShape()).isEqualTo(Shape.ONLINE);
        assertThat(result.getContent()).isEqualTo("테스트 모임");
        assertThat(result.getNumberOfPeople()).isEqualTo(2);
        assertThat(result.getPlace()).isNull();
        assertThat(result.getApply()).isEqualTo(true);

        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }

    @Test
    @DisplayName("모임 등록자가 모임을 상세조회한다.")
    void findByIdWhenLoginUserIsGatheringRegister(){
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        // when
        GatheringResponse result = gatheringService.findById(1L, 1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getGatheringTime()).isEqualTo(TEST_LOCAL_DATE_TIME1);
        assertThat(result.getShape()).isEqualTo(Shape.ONLINE);
        assertThat(result.getContent()).isEqualTo("테스트 모임");
        assertThat(result.getNumberOfPeople()).isEqualTo(1);
        assertThat(result.getPlace()).isNull();
        assertThat(result.getApply()).isNull();

        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }

    @Test
    @DisplayName("모임을 검색한다")
    void find(){
        // given
        List<Gathering> gatherings = new ArrayList<>();
        gatherings.add(TEST_GATHERING2);
        gatherings.add(TEST_GATHERING1);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Gathering> pageGathering = new PageImpl<>(gatherings, pageable,gatherings.size());

        given(gatheringRepository.findByStudyIdOrderByGatheringTimeDesc(any(),any()))
                .willReturn(pageGathering);

        // when
        Page<GatheringResponse> result = gatheringService.find(1L, pageable);

        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        then(gatheringRepository).should(times(1)).findByStudyIdOrderByGatheringTimeDesc(any(),any());
    }


    @Test
    @DisplayName("모임에 참가하지 않은 유저가 모임 참가를 한다.")
    void addGatheringUser(){
        // given
        Gathering gathering = createOnlineGathering();
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(gathering));

        // when
        gatheringService.addGatheringUser(2L,1L);

        // then
        assertThat(gathering.getGatheringUsers().size()).isEqualTo(2);
        assertThat(gathering.getNumberOfPeople()).isEqualTo(2);
        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }

    @Test
    @DisplayName("예외 테스트 : 모임에 이미 참가한 유저가 모임 참가를 하면 예외가 발생한다.")
    void addGatheringUserWhenLoginUserIsExistGatheringUser(){
        // given
        Gathering gathering = createOnlineGathering();
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(gathering));

        // when
        assertThrows(GatheringException.class,()->gatheringService.addGatheringUser(1L,1L));
    }

    @Test
    @DisplayName("모임에 참가 유저가 모임 참가를 취소한다.")
    void deleteGatheringUser(){
        // given
        Gathering gathering = createOnlineGathering();
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(gathering));

        // when
        gatheringService.deleteGatheringUser(1L,1L);

        // then
        assertThat(gathering.getGatheringUsers().size()).isEqualTo(0);
        assertThat(gathering.getNumberOfPeople()).isEqualTo(0);
        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
    }


    @Test
    @DisplayName("예외 테스트 : 모임에 참가하지 않은 유저가 모임 참가를 취소하면 예외가 발생한다.")
    void deleteGatheringUserWhenLoginUserIsNotExistGatheringUser(){
        // given
        Gathering gathering = createOnlineGathering();
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(gathering));

        // when
        assertThrows(GatheringException.class,()->gatheringService.deleteGatheringUser(2L,1L));
    }

    @Test
    @DisplayName("모임 참가자를 조회한다.")
    void findGatheringUsers() {
        // given
        given(gatheringRepository.findWithGatheringUsersById(any()))
                .willReturn(Optional.of(createOnlineGathering()));

        given(userServiceClient.findUserByIdIn(any()))
                .willReturn(Arrays.asList(TEST_USER_RESPONSE1));

        // when
        List<GatheringUserResponse> result = gatheringService.findGatheringUsers(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUserId()).isEqualTo(TEST_USER_RESPONSE1.getId());

        then(gatheringRepository).should(times(1)).findWithGatheringUsersById(any());
        then(userServiceClient).should(times(1)).findUserByIdIn(any());
    }

}
