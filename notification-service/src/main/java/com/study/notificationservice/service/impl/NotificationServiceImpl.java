package com.study.notificationservice.service.impl;

import com.study.notificationservice.client.StudyServiceClient;
import com.study.notificationservice.domain.Notification;
import com.study.notificationservice.fcm.FcmMessageSender;
import com.study.notificationservice.kafka.message.GatheringCreateMessage;
import com.study.notificationservice.model.study.StudyResponse;
import com.study.notificationservice.repository.NotificationRepository;
import com.study.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final FcmMessageSender fcmMessageSender;
    private final NotificationRepository notificationRepository;
    private final StudyServiceClient studyServiceClient;

    @Override
    @Transactional
    public void gatheringCreate(GatheringCreateMessage gatheringCreateMessage) {
        StudyResponse study = studyServiceClient
                .findWithStudyUserByStudyId(gatheringCreateMessage.getStudyId());

        String content = createGatheringNotificationMessage(gatheringCreateMessage);

        study.getStudyUsers().stream().forEach(studyUser -> {
            fcmMessageSender.send(studyUser.getFcmToken(),study.getStudyName(),content);

            Notification notification = Notification.createNotification(studyUser.getUserId(), study.getStudyName(), content);
            notificationRepository.save(notification);
        });
    }

    private String createGatheringNotificationMessage(GatheringCreateMessage gatheringCreateMessage) {
        LocalDateTime gatheringTime = gatheringCreateMessage.getGatheringTime();
        return gatheringTime.getMonthValue() + "월 " + gatheringTime.getDayOfMonth() + "일 " +
                gatheringTime.getHour() + "시 " + gatheringTime.getMinute() + "분 (" +
                gatheringCreateMessage.getShape() + ") \n" + gatheringCreateMessage.getContent();
    }


}
