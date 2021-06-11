package com.study.userservice.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.userservice.domain.User;
import com.study.userservice.domain.UserRole;
import com.study.userservice.exception.UserException;
import com.study.userservice.kafka.message.LogoutMessage;
import com.study.userservice.kafka.message.RefreshTokenCreateMessage;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserProfileUpdateRequest;
import com.study.userservice.model.UserResponse;
import com.study.userservice.model.image.ImageUploadResult;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

    @Override
    @Transactional
    public UserResponse save(UserLoginRequest request) {
        Optional<User> findUser = userRepository.findByKakaoId(request.getKakaoId());

        if (!findUser.isPresent()) {
            User savedUser = userRepository.save(
                    User.createUser(request.getKakaoId(),
                            request.getNickName(),
                            request.getThumbnailImage(),
                            request.getProfileImage(),
                            UserRole.USER));
            return UserResponse.from(savedUser);
        }

        return UserResponse.from(findUser.get());
    }

    @Override
    @Transactional
    public void updateRefreshToken(RefreshTokenCreateMessage refreshTokenCreateMessage) {
        try {
            User findUser = userRepository.findById(refreshTokenCreateMessage.getId())
                    .orElseThrow(() -> new UserException(refreshTokenCreateMessage.getId() + "는 존재하지 않는 회원입니다."));

            findUser.updateRefreshToken(refreshTokenCreateMessage.getRefreshToken());

        } catch (UserException ex) {
            log.error("{}", ex.getMessage());
        }

    }

    @Override
    public UserResponse findWithRefreshTokenById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 회원ID입니다."));

        return UserResponse.fromWithRefreshToken(findUser);
    }

    @Override
    @Transactional
    public void logout(LogoutMessage logoutMessage) {
        try {
            User findUser = userRepository.findById(logoutMessage.getUserId())
                    .orElseThrow(() -> new UserException(logoutMessage.getUserId() + "는 존재하지 않는 회원입니다."));

            findUser.logout();
        } catch (UserException ex) {
            log.error("{}", ex.getMessage());
        }
    }


    @Override
    @Transactional
    public UserResponse profileUpdate(Long userId, UserProfileUpdateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원 ID입니다."));

        findUser.changeNickName(request.getNickName());

        if(request.isDeleteImage()){
            deleteImageFromS3(findUser.getImageStoreName());
            findUser.deleteImage();
        }

        if(request.isUpdateImage()){
            validateImageType(request.getImage());
            deleteImageFromS3(findUser.getImageStoreName());
            ImageUploadResult imageUploadResult = uploadImageFromS3(request, findUser);

            findUser.changeImage(imageUploadResult.getProfileImage(),
                                imageUploadResult.getThumbnailImage(),
                                imageUploadResult.getImageStoreName());
        }

        return UserResponse.from(findUser);
    }

    private ImageUploadResult uploadImageFromS3(UserProfileUpdateRequest request, User findUser) {
        String imageStoreName = UUID.randomUUID().toString() + "_" + request.getImage().getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.getImage().getSize());
        objectMetadata.setContentType(request.getImage().getContentType());

        ImageUploadResult imageUploadResult = null;

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, imageStoreName,
                    request.getImage().getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String profileImage = amazonS3Client.getUrl(bucket, imageStoreName).toString();
            String thumbnailImage = amazonS3Client.getUrl(thumbnailBucket,imageStoreName).toString();

            imageUploadResult = ImageUploadResult.from(profileImage,thumbnailImage,imageStoreName);

        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }

        return imageUploadResult;
    }

    private void deleteImageFromS3(String imageStoreName) {
        if (imageStoreName != null) {
            amazonS3Client.deleteObject(bucket, imageStoreName);
            amazonS3Client.deleteObject(thumbnailBucket, imageStoreName);
        }
    }

    private void validateImageType(MultipartFile image) {
        if(image == null){
            throw new UserException("변경할 이미지가 존재하지 않습니다.");
        }
        // TODO TIKA 적용
        if (image.getContentType().startsWith("image") == false) {
            throw new UserException("이미지의 파일타입이 잘못되었습니다.");
        }
    }
}
