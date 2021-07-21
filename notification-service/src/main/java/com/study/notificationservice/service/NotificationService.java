package com.study.notificationservice.service;

import com.study.notificationservice.kafka.message.GatheringCreateMessage;

public interface NotificationService {

    void gatheringCreate(GatheringCreateMessage gatheringCreateMessage);

}
