package com.study.userservice.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.userservice.client.StudyServiceClient;
import com.study.userservice.domain.Image;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.exception.UserException;
import com.study.userservice.kafka.message.StudyApplyCreateMessage;
import com.study.userservice.kafka.message.StudyApplyFailMessage;
import com.study.userservice.kafka.message.StudyApplySuccessMessage;
import com.study.userservice.kafka.message.UserDeleteMessage;
import com.study.userservice.kafka.sender.UserDeleteMessageSender;
import com.study.userservice.model.interestTag.InterestTagResponse;
import com.study.userservice.model.study.StudyResponse;
import com.study.userservice.model.studyapply.StudyApplyResponse;
import com.study.userservice.model.tag.TagResponse;
import com.study.userservice.model.user.UserFindRequest;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserResponse;
import com.study.userservice.model.user.UserUpdateProfileRequest;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.repository.query.UserQueryRepository;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;
    private final StudyServiceClient studyServiceClient;

    private final UserDeleteMessageSender userDeleteMessageSender;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

    @PostConstruct
    void init() {
        IntStream.range(1, 100)
                .forEach(value -> {
                    User testUser = User.createUser(Long.valueOf(value), "황철원" + value, "10~19", "male", UserRole.USER);
                    userRepository.save(testUser);
                });
    }

    @Override
    @Transactional
    public UserResponse create(UserLoginRequest request) {
        Optional<User> findUser = userRepository.findByKakaoId(request.getKakaoId());

        if (!findUser.isPresent()) {
            User user = User.createUser(request.getKakaoId(),
                    request.getNickName(),
                    request.getAgeRange(),
                    request.getGender(),
                    UserRole.USER);
            user.changeImage(Image.createImage(request.getThumbnailImage(),
                    request.getProfileImage(),
                    null));

            User savedUser = userRepository.save(user);

            return UserResponse.from(savedUser);
        }
        return UserResponse.from(findUser.get());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, MultipartFile image, UserUpdateProfileRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원 ID입니다."));

        findUser.changeNickName(request.getNickName());

        Image updateImage = updateImage(image, request.isDeleteImage(), findUser);

        findUser.changeImage(updateImage);

        return UserResponse.from(findUser);
    }

    private Image updateImage(MultipartFile image, boolean deleteImage, User user) {
        Image userImage = user.getImage();

        log.info("deleteImage = {}",deleteImage);
        log.info("image {}", image != null);

        if (deleteImage && image != null) {
            log.info("11");
            if (userImage != null) {
                deleteImageFromS3(userImage.getImageStoreName());
                userImage = null;
            }
        }
        if (!deleteImage && image != null) {
            if (!image.isEmpty()) {
                if (userImage != null) {
                    deleteImageFromS3(userImage.getImageStoreName());
                }
                validateImageType(image);
                userImage = uploadImageToS3(image);
            }
        }
        return userImage;
    }

    @Override
    public UserResponse findById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원 ID 입니다."));
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원 ID 입니다."));

        userRepository.delete(findUser);

        userDeleteMessageSender.send(UserDeleteMessage.from(findUser));
    }

    @Override
    public List<UserResponse> findByIdIn(UserFindRequest request) {
        return userQueryRepository.findByIdIn(request.getUserIdList()).stream()
                .map(user -> UserResponse.from(user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateLocation(Long userId, Long locationId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원 ID입니다."));

        findUser.changeLocation(locationId);
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public void addInterestTag(Long userId, Long tagId) {
        User findUser = userQueryRepository.findWithInterestTagById(userId);

        findUser.checkExistTag(tagId);
        findUser.addInterestTag(tagId);
    }

    @Override
    @Transactional
    public void deleteInterestTag(Long userId, Long tagId) {
        User findUser = userQueryRepository.findWithInterestTagById(userId);

        findUser.deleteInterestTag(tagId);
    }

    @Override
    public List<InterestTagResponse> findInterestTagByUserId(Long userId) {
        User findUser = userQueryRepository.findWithInterestTagById(userId);

        List<Long> tagIdList = findUser.getInterestTags().stream()
                .map(interestTag -> interestTag.getTagId())
                .collect(Collectors.toList());

        List<TagResponse> tags = studyServiceClient.findTagsByIdIn(tagIdList);

        List<InterestTagResponse> interestTagResponses = new ArrayList<>();

        findUser.getInterestTags().stream()
                .forEach(interestTag -> {
                    for (TagResponse tag : tags) {
                        if (interestTag.getTagId().equals(tag.getId())) {
                            interestTagResponses.add(InterestTagResponse.from(interestTag, tag));
                        }
                    }
                });

        return interestTagResponses;
    }

    @Override
    @Transactional
    public void createStudyApply(StudyApplyCreateMessage studyApplyCreateMessage) {
        User findUser = userRepository.findById(studyApplyCreateMessage.getUserId())
                .orElseThrow(() -> new UserException(studyApplyCreateMessage.getUserId() + "는 존재하지 않는 회원 ID입니다."));

        findUser.addStudyApply(studyApplyCreateMessage.getStudyId());
    }

    @Override
    @Transactional
    public void SuccessStudyApply(StudyApplySuccessMessage studyApplySuccessMessage) {
        User findUser = userQueryRepository
                .findWithStudyApplyById(studyApplySuccessMessage.getUserId());

        findUser.successStudyApply(studyApplySuccessMessage.getStudyId());
    }

    @Override
    @Transactional
    public void FailStudyApply(StudyApplyFailMessage studyApplyFailMessage) {
        User findUser = userQueryRepository
                .findWithStudyApplyById(studyApplyFailMessage.getUserId());

        findUser.failStudyApply(studyApplyFailMessage.getStudyId());
    }

    @Override
    @Transactional
    public List<StudyApplyResponse> findStudyAppliesByUserId(Long userId) {
        User findUser = userQueryRepository.findWithStudyApplyById(userId);

        List<Long> studyIdList = findUser.getStudyApplies().stream()
                .map(studyApply -> studyApply.getStudyId())
                .collect(Collectors.toList());

        List<StudyResponse> studyList = studyServiceClient.findStudiesByIdIn(studyIdList);

        List<StudyApplyResponse> studyApplyResponses = new ArrayList<>();
        findUser.getStudyApplies().stream()
                .forEach(studyApply -> {
                    for (StudyResponse study : studyList) {
                        if (studyApply.getStudyId().equals(study.getId())) {
                            studyApplyResponses.add(StudyApplyResponse.from(studyApply, study));
                            break;
                        }
                    }
                });

        findUser.deleteStudyApply();

        return studyApplyResponses;
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

            uploadResult = Image.createImage(thumbnailImage, profileImage, imageStoreName);

        } catch (Exception e) {
            throw new UserException(e.getMessage());
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
            throw new UserException("이미지의 파일타입이 잘못되었습니다.");
        }
    }
}
