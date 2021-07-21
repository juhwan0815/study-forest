package com.study.notificationservice.service.impl;

import com.study.notificationservice.client.StudyServiceClient;
import com.study.notificationservice.client.UserServiceClient;
import com.study.notificationservice.domain.Notification;
import com.study.notificationservice.fcm.FcmMessageSender;
import com.study.notificationservice.kafka.message.GatheringCreateMessage;
import com.study.notificationservice.kafka.message.StudyApplyFailMessage;
import com.study.notificationservice.kafka.message.StudyApplySuccessMessage;
import com.study.notificationservice.model.study.StudyResponse;
import com.study.notificationservice.model.user.UserResponse;
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
    private final UserServiceClient userServiceClient;

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

    @Override
    @Transactional
    public void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage) {
        UserResponse user = userServiceClient.findUserById(studyApplyFailMessage.getUserId());

        String content = createStudyApplyFailMessage(studyApplyFailMessage.getStudyName());

        fcmMessageSender.send(user.getFcmToken(),studyApplyFailMessage.getStudyName(),content);

        Notification notification = Notification.
                createNotification(user.getId(), studyApplyFailMessage.getStudyName(), content);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage) {
        UserResponse user = userServiceClient.findUserById(studyApplySuccessMessage.getUserId());

        String content = createStudyApplySuccessMessage(studyApplySuccessMessage.getStudyName());

        fcmMessageSender.send(user.getFcmToken(),studyApplySuccessMessage.getStudyName(),content);

        Notification notification = Notification.
                createNotification(user.getId(), studyApplySuccessMessage.getStudyName(), content);
        notificationRepository.save(notification);
    }

    private String createStudyApplySuccessMessage(String studyName) {
        return studyName + " 참가 신청이 수락되었습니다.";
    }

    private String createStudyApplyFailMessage(String studyName) {
        return studyName + " 참가 신청이 거절되었습니다.";
    }

    private String createGatheringNotificationMessage(GatheringCreateMessage gatheringCreateMessage) {
        LocalDateTime gatheringTime = gatheringCreateMessage.getGatheringTime();
        return gatheringTime.getMonthValue() + "월 " + gatheringTime.getDayOfMonth() + "일 " +
                gatheringTime.getHour() + "시 " + gatheringTime.getMinute() + "분 (" +
                gatheringCreateMessage.getShape() + ") \n" + gatheringCreateMessage.getContent();
    }


}
