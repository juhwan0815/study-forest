package com.study.service;

import com.study.client.StudyServiceClient;
import com.study.client.UserResponse;
import com.study.client.UserServiceClient;
import com.study.domain.Notification;
import com.study.dto.NotificationResponse;
import com.study.fcm.FcmMessageSender;
import com.study.kakfa.GatheringCreateMessage;
import com.study.kakfa.StudyApplyFailMessage;
import com.study.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserServiceClient userServiceClient;
    private final StudyServiceClient studyServiceClient;

    private final FcmMessageSender fcmMessageSender;

    @Override
    public Slice<NotificationResponse> findByUserId(Long userId, Pageable pageable) {
        Slice<Notification> notifications = notificationRepository.findByUserIdOrOrderById(pageable, userId);
        return notifications.map(notification -> NotificationResponse.from(notification));
    }

    @Override
    @Transactional
    public void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage) {
        UserResponse user = userServiceClient.findById(studyApplyFailMessage.getUserId());

        String content = createStudyApplyFailMessage(studyApplyFailMessage.getStudyName());
        Notification notification = Notification.createNotification(user.getUserId(), studyApplyFailMessage.getStudyName(), content);
        notificationRepository.save(notification);

        fcmMessageSender.send(user.getFcmToken(), studyApplyFailMessage.getStudyName(), content);
    }

    private String createChatTitle(String studyName, String chatRoomName) {
        return studyName + " - " + chatRoomName;
    }

    private String createChatContent(String nickName, String content) {
        return nickName + " : " + content;
    }

    private String createStudyCreateMessage(String tagName, String studyName) {
        return tagName + "주제의 스터디가 개설되었습니다.\n " + studyName;
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
                ") \n" + gatheringCreateMessage.getContent();
    }
}
