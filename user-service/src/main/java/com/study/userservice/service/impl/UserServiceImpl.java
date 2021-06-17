package com.study.userservice.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.userservice.domain.Image;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.exception.UserException;
import com.study.userservice.kafka.message.UserDeleteMessage;
import com.study.userservice.kafka.message.UserUpdateProfileMessage;
import com.study.userservice.kafka.sender.UserDeleteMessageSender;
import com.study.userservice.kafka.sender.UserUpdateProfileMessageSender;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserUpdateProfileRequest;
import com.study.userservice.model.user.UserResponse;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;

    private final UserDeleteMessageSender userDeleteMessageSender;
    private final UserUpdateProfileMessageSender userUpdateProfileMessageSender;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

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

        if (request.isDeleteImage() && image.isEmpty()) {
            if (findUser.getImage() != null) {
                deleteImageFromS3(findUser.getImage().getImageStoreName());
                findUser.changeImage(null);
            }
        }
        if (!request.isDeleteImage() && !image.isEmpty()) {
            if (findUser.getImage() != null) {
                deleteImageFromS3(findUser.getImage().getImageStoreName());
            }
            validateImageType(image);
            findUser.changeImage(uploadImageFromS3(image));
        }

        userUpdateProfileMessageSender.send(UserUpdateProfileMessage.from(findUser));

        return UserResponse.from(findUser);
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

    private Image uploadImageFromS3(MultipartFile image) {
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
