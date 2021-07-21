package com.study.gatheringservice.kafka.sender;

import com.study.gatheringservice.kafka.message.GatheringCreateMessage;

public interface GatheringCreateMessageSender {

    void send(GatheringCreateMessage gatheringCreateMessage);
}
