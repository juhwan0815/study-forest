package com.study.kakfa.listener;

import com.study.kakfa.UserDeleteMessage;
import com.study.service.StudyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserDeleteListenerTest {

    @InjectMocks
    private UserDeleteListener userDeleteListener;

    @Mock
    private StudyService studyService;

    @Test
    @DisplayName("회원 탈퇴 메세지를 수신한다.")
    void studyDeleteListen(){
        // give
        willDoNothing()
                .given(studyService)
                .deleteStudyUserAndWaitUser(any());

        // when
        userDeleteListener.userDeleteListen(UserDeleteMessage.from(1L), null);

        // then
        then(studyService).should(times(1)).deleteStudyUserAndWaitUser(any());
    }


}