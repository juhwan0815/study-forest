package com.study.studyservice.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.studyservice.client.LocationServiceClient;
import com.study.studyservice.client.UserServiceClient;
import com.study.studyservice.domain.*;
import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.exception.StudyException;
import com.study.studyservice.kafka.message.StudyApplyCreateMessage;
import com.study.studyservice.kafka.message.StudyDeleteMessage;
import com.study.studyservice.kafka.sender.StudyApplyCreateMessageSender;
import com.study.studyservice.kafka.sender.StudyDeleteMessageSender;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.model.user.UserResponse;
import com.study.studyservice.model.waituser.WaitUserResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.repository.StudyRepository;
import com.study.studyservice.repository.query.StudyQueryRepository;
import com.study.studyservice.service.StudyService;
import com.study.studyservice.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final TagService tagService;

    private final LocationServiceClient locationServiceClient;
    private final UserServiceClient userServiceClient;
    private final AmazonS3Client amazonS3Client;

    private final StudyRepository studyRepository;
    private final CategoryRepository categoryRepository;
    private final StudyQueryRepository studyQueryRepository;

    private final StudyDeleteMessageSender studyDeleteMessageSender;
    private final StudyApplyCreateMessageSender studyApplyCreateMessageSender;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

    @PostConstruct
    void init() {
        IntStream.range(1, 5).forEach(
                value -> {
                    Category testParentCategory = Category.createCategory("개발" + value, null);
                    categoryRepository.save(testParentCategory);
                }
        );

        IntStream.range(1, 10).forEach(
                value -> {
                    int revalue = value % 3 + 1;
                    Category parentCategory = categoryRepository.findById(Long.valueOf(revalue)).get();
                    Category testParentCategory = Category.createCategory("백엔드" + revalue, parentCategory);
                    categoryRepository.save(testParentCategory);
                }
        );

        IntStream.range(1, 10)
                .forEach(value -> {
                    Category category = categoryRepository.findById(9L).get();
                    Study study = Study.createStudy("테스트 스터디" + value,
                            5,
                            "테스트 스터디" + value,
                            true,
                            true,
                            category);
                    study.addWaitUser(Long.valueOf(1 + 10 * value));
                    study.addWaitUser(Long.valueOf(2 + 10 * value));
                    study.addWaitUser(Long.valueOf(3 + 10 * value));
                    study.addWaitUser(Long.valueOf(4 + 10 * value));
                    study.addWaitUser(Long.valueOf(5 + 10 * value));
                    study.addWaitUser(Long.valueOf(6 + 10 * value));
                    study.addWaitUser(Long.valueOf(7 + 10 * value));
                    study.addWaitUser(Long.valueOf(8 + 10 * value));
                    study.addWaitUser(Long.valueOf(9 + 10 * value));
                    Study savedStudy = studyRepository.save(study);
                });
    }


    @Override
    @Transactional
    public StudyResponse create(Long userId, MultipartFile image, StudyCreateRequest request) {
        Category findCategory = categoryRepository.findWithParentById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(request.getCategoryId() + "는 존재하지 않는 카테고리 ID입니다."));

        LocationResponse location = getLocation(request.isOffline(), request.getLocationCode());

        List<Tag> tags = tagService.FindAndCreate(request.getTags());

        Image studyImage = uploadImage(image);

        Study study = Study.createStudy(request.getName(),
                request.getNumberOfPeople(),
                request.getContent(),
                request.isOnline(),
                request.isOffline(),
                findCategory);

        study.addStudyUser(userId, Role.ADMIN);
        study.changeImage(studyImage);
        study.addStudyTags(tags);
        study.changeLocation(location.getId());

        Study savedStudy = studyRepository.save(study);

        return StudyResponse.from(savedStudy, location);
    }

    @Override
    @Transactional
    public StudyResponse update(Long userId, Long studyId, MultipartFile image, StudyUpdateRequest request) {
        Study findStudy = studyQueryRepository.findWithStudyTagsById(studyId);

        findStudy.checkStudyAdmin(userId);
        findStudy.checkNumberOfStudyUser(request.getNumberOfPeople());

        Category findCategory = categoryRepository.findWithParentById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(request.getCategoryId() + "는 존재하지 않는 카테고리입니다."));

        LocationResponse location = getLocation(request.isOffline(), request.getLocationCode());
        List<Tag> tags = tagService.FindAndCreate(request.getTags());

        Image studyImage = updateImage(image, request.isDeleteImage(), findStudy);

        findStudy.update(request.getName(),
                request.getNumberOfPeople(),
                request.getContent(),
                request.isOnline(),
                request.isOffline(),
                request.isClose(),
                findCategory);

        findStudy.changeImage(studyImage);
        findStudy.updateStudyTags(tags);
        findStudy.changeLocation(location.getId());

        return StudyResponse.from(findStudy, location);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long studyId) {
        Study findStudy = studyQueryRepository.findWithStudyUsersById(studyId);

        findStudy.checkStudyAdmin(userId);

        studyRepository.delete(findStudy);

        studyDeleteMessageSender.send(StudyDeleteMessage.from(findStudy));
    }

    @Override
    public StudyResponse findById(Long studyId) {
        Study findStudy = studyQueryRepository.findWithCategoryAndStudyTagsAndTagById(studyId);

        LocationResponse location;

        if (findStudy.getLocationId() != null && findStudy.isOffline()) {
            location = locationServiceClient.findLocationById(findStudy.getLocationId());
        } else {
            location = new LocationResponse();
        }

        return StudyResponse.from(findStudy, location);
    }

    @Override
    @Transactional
    public void createWaitUser(Long userId, Long studyId) {
        Study findStudy = studyQueryRepository.findWithWaitUserById(studyId);

        findStudy.checkExistWaitUserAndStudyUser(userId);

        findStudy.addWaitUser(userId);

        studyApplyCreateMessageSender.send(StudyApplyCreateMessage.from(userId, studyId));
    }

    @Override
    public List<WaitUserResponse> findWaitUsersByStudyId(Long studyId) {
        Study findStudy = studyQueryRepository.findWithWaitUserById(studyId);

        List<Long> userIdList = findStudy.getWaitUsers().stream()
                .map(waitUser -> waitUser.getUserId())
                .collect(Collectors.toList());

        List<UserResponse> userList = userServiceClient.findUserByIdIn(userIdList);

        List<WaitUserResponse> waitUserResponses = new ArrayList<>();
        findStudy.getWaitUsers().stream()
                .forEach(waitUser -> {
                    for (UserResponse user : userList) {
                        if (waitUser.getUserId().equals(user.getId())) {
                            waitUserResponses.add(WaitUserResponse.from(waitUser, user));
                            break;
                        }
                    }
                });
        return waitUserResponses;
    }

    private LocationResponse getLocation(boolean offline, String locationCode) {
        LocationResponse location = null;
        if (offline) {
            if (locationCode == null) {
                throw new StudyException("지역정보 코드가 존재하지 않습니다.");
            }
            location = locationServiceClient
                    .findLocationByCode(locationCode);
        } else {
            location = new LocationResponse();
        }
        return location;
    }

    private Image updateImage(MultipartFile image, boolean deleteImage, Study study) {
        Image studyImage = study.getImage();
        if (deleteImage && image.isEmpty()) {
            if (studyImage != null) {
                deleteImageFromS3(studyImage.getImageStoreName());
                studyImage = null;
            }
        }
        if (!deleteImage && !image.isEmpty()) {
            if (studyImage != null) {
                deleteImageFromS3(studyImage.getImageStoreName());
            }
            validateImageType(image);
            studyImage = uploadImageToS3(image);
        }
        return studyImage;
    }

    private Image uploadImage(MultipartFile image) {
        Image uploadResult = null;
        if (!image.isEmpty()) {
            validateImageType(image);
            uploadResult = uploadImageToS3(image);
        }
        return uploadResult;
    }

    private Image uploadImageToS3(MultipartFile image) {
        String imageStoreName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        Image uploadResult = null;

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, imageStoreName, image.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            String profileImage = amazonS3Client.getUrl(bucket, imageStoreName).toString();
            String thumbnailImage = amazonS3Client.getUrl(thumbnailBucket, imageStoreName).toString();

            uploadResult = Image.createImage(profileImage, thumbnailImage, imageStoreName);

        } catch (Exception e) {
            throw new StudyException(e.getMessage());
        }

        return uploadResult;
    }

    private void deleteImageFromS3(String imageStoreName) {
        if (imageStoreName != null) {
            amazonS3Client.deleteObject(bucket, imageStoreName);
            amazonS3Client.deleteObject(thumbnailBucket, imageStoreName);
        }
    }

    private void validateImageType(MultipartFile image) {
        // TODO TIKA 적용
        if (image.getContentType().startsWith("image") == false) {
            throw new StudyException("이미지의 파일타입이 잘못되었습니다.");
        }
    }


}
