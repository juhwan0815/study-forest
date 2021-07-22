package com.study.notificationservice.service.impl;

import com.study.notificationservice.client.StudyServiceClient;
import com.study.notificationservice.client.UserServiceClient;
import com.study.notificationservice.domain.Notification;
import com.study.notificationservice.fcm.FcmMessageSender;
import com.study.notificationservice.kafka.message.*;
import com.study.notificationservice.model.study.StudyResponse;
import com.study.notificationservice.model.study.StudyUserResponse;
import com.study.notificationservice.model.tag.InterestTagResponse;
import com.study.notificationservice.model.tag.TagResponse;
import com.study.notificationservice.model.user.UserResponse;
import com.study.notificationservice.model.user.UserWithTagResponse;
import com.study.notificationservice.repository.NotificationRepository;
import com.study.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            fcmMessageSender.send(studyUser.getFcmToken(), study.getStudyName(), content);

            Notification notification = Notification.createNotification(studyUser.getUserId(), study.getStudyName(), content);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage) {
        UserResponse user = userServiceClient.findUserById(studyApplyFailMessage.getUserId());

        String content = createStudyApplyFailMessage(studyApplyFailMessage.getStudyName());

        fcmMessageSender.send(user.getFcmToken(), studyApplyFailMessage.getStudyName(), content);

        Notification notification = Notification.
                createNotification(user.getId(), studyApplyFailMessage.getStudyName(), content);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage) {
        UserResponse user = userServiceClient.findUserById(studyApplySuccessMessage.getUserId());

        String content = createStudyApplySuccessMessage(studyApplySuccessMessage.getStudyName());

        fcmMessageSender.send(user.getFcmToken(), studyApplySuccessMessage.getStudyName(), content);

        Notification notification = Notification.
                createNotification(user.getId(), studyApplySuccessMessage.getStudyName(), content);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void studyCreate(StudyCreateMessage studyCreateMessage) {
        List<TagResponse> tags = studyCreateMessage.getTags();
        List<Long> tagIdList = tags.stream()
                .map(tagResponse -> tagResponse.getId())
                .collect(Collectors.toList());

        List<UserWithTagResponse> users = userServiceClient.findWithInterestTagsByTagIdList(tagIdList);

        tags.stream().forEach(tag -> {
            String content = createStudyCreateMessage(tag.getName(), studyCreateMessage.getStudyName());
            for (UserWithTagResponse user : users) {
                for (InterestTagResponse interestTag : user.getTags()) {
                    if (interestTag.getTagId().equals(tag.getId())) {
                        fcmMessageSender.send(user.getFcmToken(), "스터디 생성 알림", content);
                        Notification notification = Notification.createNotification(user.getId(), "스터디 생성 알림", content);
                        notificationRepository.save(notification);
                    }
                }
            }
        });
    }

    @Override
    public void chatCreate(ChatCreateMessage chatCreateMessage) {
        StudyResponse study = studyServiceClient
                .findWithStudyUserByStudyId(chatCreateMessage.getStudyId());

        List<Long> userIdList = chatCreateMessage.getUserIdList();
        String chatTitle = createChatTitle(study.getStudyName(), chatCreateMessage.getChatRoomName());
        String chatContent = createChatContent(chatCreateMessage.getNickName(), chatCreateMessage.getContent());
        userIdList.stream().forEach(userId -> {
            for (StudyUserResponse studyUser : study.getStudyUsers()) {
                if (studyUser.getUserId().equals(userId)) {
                    fcmMessageSender.send(studyUser.getFcmToken(),chatTitle,chatContent);
                    break;
                }
            }
        });
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
                gatheringCreateMessage.getShape() + ") \n" + gatheringCreateMessage.getContent();
    }


}
