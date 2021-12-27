package com.study.kakfa.sender;


import com.study.kakfa.GatheringCreateMessage;

public interface GatheringCreateMessageSender {

    void send(GatheringCreateMessage gatheringCreateMessage);
}
