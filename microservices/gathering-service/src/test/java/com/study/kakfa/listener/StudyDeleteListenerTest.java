package com.study.kakfa.listener;

import com.study.kakfa.StudyDeleteMessage;
import com.study.service.GatheringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudyDeleteListenerTest {

    @InjectMocks
    private StudyDeleteListener studyDeleteListener;

    @Mock
    private GatheringService gatheringService;

    @Test
    @DisplayName("스터디 삭제 메세지를 수신한다.")
    void studyDeleteListen() {
        // given
        willDoNothing()
                .given(gatheringService)
                .deleteByStudyId(any());

        // when
        studyDeleteListener.studyDeleteListen(StudyDeleteMessage.from(1L, Arrays.asList(1L)), null);

        // then
        then(gatheringService).should(times(1)).deleteByStudyId(any());
    }
}