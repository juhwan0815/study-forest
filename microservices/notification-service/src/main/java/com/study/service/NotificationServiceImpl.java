package com.study.service;

import com.study.client.*;
import com.study.domain.Notification;
import com.study.dto.NotificationResponse;
import com.study.fcm.FcmMessageSender;
import com.study.kakfa.*;
import com.study.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.graalvm.compiler.replacements.StandardGraphBuilderPlugins;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        Slice<Notification> notifications = notificationRepository.findByUserIdOrderById(pageable, userId);
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

    @Override
    @Transactional
    public void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage) {
        UserResponse user = userServiceClient.findById(studyApplySuccessMessage.getUserId());

        String content = createStudyApplySuccessMessage(studyApplySuccessMessage.getStudyName());
        Notification notification = Notification.
                createNotification(user.getUserId(), studyApplySuccessMessage.getStudyName(), content);
        notificationRepository.save(notification);

        fcmMessageSender.send(user.getFcmToken(), studyApplySuccessMessage.getStudyName(), content);
    }

    @Override
    @Transactional
    public void gatheringCreate(GatheringCreateMessage gatheringCreateMessage) {
        StudyResponse study = studyServiceClient.findById(gatheringCreateMessage.getStudyId());
        List<UserResponse> studyUsers = studyServiceClient.findStudyUsersById(gatheringCreateMessage.getStudyId());

        String content = createGatheringNotificationMessage(gatheringCreateMessage);
        studyUsers.forEach(studyUser -> {
            fcmMessageSender.send(studyUser.getFcmToken(), study.getName(), content);

            Notification notification = Notification.createNotification(studyUser.getUserId(), study.getName(), content);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void messageCreate(MessageCreateMessage messageCreateMessage) {
        StudyResponse study = studyServiceClient.findByChatRoomId(messageCreateMessage.getRoomId());
        ChatRoomResponse chatRoom = studyServiceClient.findChatRoomByIdAndChatRoomId(study.getStudyId(), messageCreateMessage.getRoomId());
        List<UserResponse> studyUsers = studyServiceClient.findStudyUsersById(study.getStudyId());

        List<Long> userIds = messageCreateMessage.getUserIds();
        String messageTitle = createMessageTitle(study.getName(), chatRoom.getName());
        String messageContent = createMessageContent(messageCreateMessage.getSender(), messageCreateMessage.getContent());
        studyUsers.forEach(studyUser -> {
            boolean matchResult = false;
            for (Long userId : userIds) {
                if (studyUser.getUserId().equals(userId)) {
                    matchResult = true;
                    break;
                }
            }
            if (!matchResult) {
                fcmMessageSender.send(studyUser.getFcmToken(), messageTitle, messageContent);
            }
        });
    }

    @Override
    @Transactional
    public void studyCreate(StudyCreateMessage studyCreateMessage) {
        studyCreateMessage.getTags().forEach(tag -> {
            List<UserResponse> users = userServiceClient.findByKeywordContentContain(tag);
            String content = createStudyCreateMessage(tag, studyCreateMessage.getName());

            for (UserResponse user : users) {
                Notification notification = Notification.createNotification(user.getUserId(), "스터디 생성 알림", content);
                notificationRepository.save(notification);

                fcmMessageSender.send(user.getFcmToken(), "스터디 생성 알림", content);
            }
        });
    }


    private String createStudyCreateMessage(String tagName, String studyName) {
        return tagName + "키워드의 스터디가 개설되었습니다.\n " + studyName;
    }

    private String createMessageTitle(String studyName, String chatRoomName) {
        return studyName + " - " + chatRoomName;
    }

    private String createMessageContent(String nickName, String content) {
        return nickName + " : " + content;
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
