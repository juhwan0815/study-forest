package com.study.kakfa.listener;

import com.study.kakfa.UserDeleteMessage;
import com.study.service.GatheringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserDeleteListenerTest {

    @InjectMocks
    private UserDeleteListener userDeleteListener;

    @Mock
    private GatheringService gatheringService;

    @Test
    @DisplayName("회원 탈퇴 메세지를 수신한다.")
    void userDeleteListen() {
        // given
        willDoNothing()
                .given(gatheringService)
                .deleteGatheringUser(any());

        // when
        userDeleteListener.userDeleteListen(UserDeleteMessage.from(1L), null);

        // then
        then(gatheringService).should(times(1)).deleteGatheringUser(any());
    }
}

