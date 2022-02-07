package com.study.service;

import com.study.client.*;
import com.study.domain.Category;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.exception.AccessDeniedException;
import com.study.exception.NotFoundException;
import com.study.kakfa.*;
import com.study.kakfa.sender.*;
import com.study.repository.CategoryRepository;
import com.study.repository.StudyQueryRepository;
import com.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.exception.NotFoundException.CATEGORY_NOT_FOUND;
import static com.study.exception.NotFoundException.STUDY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final StudyQueryRepository studyQueryRepository;
    private final CategoryRepository categoryRepository;
    private final AreaServiceClient areaServiceClient;
    private final UserServiceClient userServiceClient;
    private final AwsClient awsClient;

    private final StudyCreateMessageSender studyCreateMessageSender;
    private final StudyDeleteMessageSender studyDeleteMessageSender;
    private final StudyApplyFailMessageSender studyApplyFailMessageSender;
    private final StudyApplySuccessMessageSender studyApplySuccessMessageSender;
    private final ChatRoomDeleteMessageSender chatRoomDeleteMessageSender;

    @Override
    @Transactional
    public Long create(Long userId, StudyCreateRequest request) {
        Category findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND)));

        Study study = Study.createStudy(request.getName(), request.getContent(), request.getNumberOfPeople(),
                request.getOnline(), request.getOffline(), request.getImageUrl(), findCategory);
        study.addStudyUser(userId, StudyRole.ADMIN);
        study.changeTags(request.getTags());

        if (request.getOffline() == true) {
            AreaResponse area = areaServiceClient.findByCode(request.getAreaCode());
            study.changeArea(area.getId());
        }

        Study savedStudy = studyRepository.save(study);
        studyCreateMessageSender.send(StudyCreateMessage.from(study.getId(), study.getName(), request.getTags()));
        return savedStudy.getId();
    }

    @Override
    @Transactional
    public void update(Long userId, Long studyId, StudyUpdateRequest request) {
        Study findStudy = studyRepository.findWithTagById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        Category findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));


        findStudy.update(request.getName(), request.getContent(), request.getNumberOfPeople(),
                request.getOnline(), request.getOffline(), request.getOpen(), request.getImageUrl(), findCategory);
        findStudy.changeTags(request.getTags());

        if (request.getOffline() == true) {
            AreaResponse area = areaServiceClient.findByCode(request.getAreaCode());
            findStudy.changeArea(area.getId());
        }
    }

    @Override
    @Transactional
    public void delete(Long userId, Long studyId) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        List<Long> chatRoomIds = findStudy.getChatRoomsId();
        studyRepository.delete(findStudy);
        studyDeleteMessageSender.send(StudyDeleteMessage.from(findStudy.getId(), chatRoomIds));
    }

    @Override
    public StudyResponse findById(Long studyId) {
        Study findStudy = studyRepository.findWithCategoryAndTagById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        AreaResponse areaResponse = null;
        if (findStudy.isOffline()) {
            areaResponse = areaServiceClient.findById(findStudy.getAreaId());
        }

        return StudyResponse.from(findStudy, areaResponse);
    }

    @Override
    public List<StudyResponse> search(Long userId, StudySearchRequest request) {
        List<Long> areaIds = null;
        if (request.getOffline() && userId != null) {
            UserResponse user = userServiceClient.findById(userId);
            if (user.getAreaId() != null) {
                List<AreaResponse> areas = areaServiceClient.findAroundById(user.getAreaId(), user.getDistance());
                areaIds = areas.stream().map(area -> area.getId()).collect(Collectors.toList());
            }
        }
        return studyQueryRepository.findBySearchCondition(request, areaIds);
    }

    @Override
    @Transactional
    public void createWaitUser(Long userId, Long studyId) {
        Study findStudy = studyRepository.findWithWaitUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));
        findStudy.addWaitUser(userId);
    }

    @Override
    @Transactional
    public void deleteWaitUser(Long userId, Long studyId) {
        Study findStudy = studyRepository.findWithWaitUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));
        findStudy.deleteWaitUser(userId);
    }

    @Override
    @Transactional
    public void failWaitUser(Long userId, Long studyId, Long waitUserId) {
        Study findStudy = studyRepository.findWithWaitUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        findStudy.failWaitUser(waitUserId);

        studyApplyFailMessageSender.send(StudyApplyFailMessage.from(waitUserId, findStudy.getId(), findStudy.getName()));
    }

    @Override
    public List<UserResponse> findWaitUsersById(Long studyId) {
        Study findStudy = studyRepository.findWithWaitUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        List<Long> userIds = findStudy.getWaitUsersId();
        return userServiceClient.findByIdIn(userIds);
    }

    @Override
    @Transactional
    public void createStudyUser(Long userId, Long studyId, Long studyUserId) {
        Study findStudy = studyRepository.findWithWaitUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        findStudy.successWaitUser(studyUserId);
        findStudy.addStudyUser(studyUserId, StudyRole.USER);
        studyApplySuccessMessageSender.send(StudyApplySuccessMessage.from(studyUserId, studyId, findStudy.getName()));
    }

    @Override
    @Transactional
    public void deleteStudyUser(Long userId, Long studyId, Long studyUserId) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        findStudy.deleteStudyUser(studyUserId);
    }

    @Override
    @Transactional
    public void deleteStudyUser(Long userId, Long studyId) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        findStudy.deleteStudyUser(userId);
    }

    @Override
    public List<StudyUserResponse> findStudyUsersById(Long studyId) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));
        List<Long> userIds = findStudy.getStudyUsersId();

        List<UserResponse> users = userServiceClient.findByIdIn(userIds);

        return findStudy.getStudyUsers().stream()
                .map(studyUser -> StudyUserResponse.from(studyUser, users))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createChatRoom(Long userId, Long studyId, ChatRoomCreateRequest request) {
        Study findStudy = studyRepository.findWithChatRoomById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        findStudy.addChatRoom(request.getName());
    }

    @Override
    @Transactional
    public void updateChatRoom(Long userId, Long studyId, Long chatRoomId, ChatRoomUpdateRequest request) {
        Study findStudy = studyRepository.findWithChatRoomById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        findStudy.updateChatRoom(chatRoomId, request.getName());
    }

    @Override
    @Transactional
    public void deleteChatRoom(Long userId, Long studyId, Long chatRoomId) {
        Study findStudy = studyRepository.findWithChatRoomById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (!findStudy.isStudyAdmin(userId)) {
            throw new AccessDeniedException();
        }

        findStudy.deleteChatRoom(chatRoomId);
        chatRoomDeleteMessageSender.send(ChatRoomDeleteMessage.from(chatRoomId));
    }

    @Override
    public List<ChatRoomResponse> findChatRoomsById(Long studyId) {
        Study findStudy = studyRepository.findWithChatRoomById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        return findStudy.getChatRooms().stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyResponse> findByUserId(Long userId) {
        return studyQueryRepository.findByStudyUserId(userId);
    }

    @Override
    public List<StudyResponse> findByWaitUserId(Long userId) {
        return studyQueryRepository.findByWaitUserId(userId);
    }

    @Override
    public StudyResponse findByChatRoomId(Long chatRoomId) {
        Study findStudy = studyRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));
        return StudyResponse.from(findStudy);
    }

    @Override
    public ChatRoomResponse findChatRoomByIdAndChatRoomId(Long studyId, Long chatRoomId) {
        Study findStudy = studyRepository.findWithChatRoomById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));
        return ChatRoomResponse.from(findStudy.getChatRoom(chatRoomId));
    }

    @Override
    public String uploadImage(MultipartFile image) {
        return awsClient.upload(image);
    }
}
