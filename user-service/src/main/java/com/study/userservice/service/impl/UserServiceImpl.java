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
import com.study.userservice.model.UserImageUpdateRequest;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserNickNameUpdateRequest;
import com.study.userservice.model.UserResponse;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    public UserResponse imageUpdate(Long userId, UserImageUpdateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원ID입니다."));

        validateFileType(request.getImage());
        deleteImage(findUser);
        uploadImage(findUser, request.getImage());
        uploadThumbnailImage(findUser, request.getImage());

        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public UserResponse nickNameUpdate(Long userId, UserNickNameUpdateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(userId + "는 존재하지 않는 회원ID입니다."));

        if(userRepository.findByNickName(request.getNickName()).isPresent()){
            throw new UserException(request.getNickName() + "은 이미 사용중인 닉네임입니다.");
        }

        findUser.changeNickName(request.getNickName());

        return UserResponse.from(findUser);
    }

    private void deleteImage(User user) {
        if (user.getProfileImageStoreName() != null && user.getThumbnailImageStoreName() != null) {
            amazonS3Client.deleteObject(bucket, user.getProfileImageStoreName());
            amazonS3Client.deleteObject(bucket, user.getThumbnailImageStoreName());
        }
    }

    private void uploadImage(User user, MultipartFile image) {
        String storeName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, storeName, image.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String imageStoreUrl = amazonS3Client.getUrl(bucket, storeName).toString();

            user.changeImage(imageStoreUrl,storeName);
        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }
    }

    private void uploadThumbnailImage(User user, MultipartFile image) {
        try {

            // make thumbnail image for s3
            BufferedImage bufferImage = ImageIO.read(image.getInputStream());
            BufferedImage thumbnailImage = Thumbnails.of(bufferImage)
                    .size(110, 110)
                    .asBufferedImage();

            ByteArrayOutputStream thumbOutPut = new ByteArrayOutputStream();
            String imageType = image.getContentType();
            ImageIO.write(thumbnailImage, imageType.substring(imageType.indexOf("/") + 1), thumbOutPut);

            // set Metadata
            ObjectMetadata thumbObjectMetadata = new ObjectMetadata();
            byte[] thumbBytes = thumbOutPut.toByteArray();
            thumbObjectMetadata.setContentLength(thumbBytes.length);
            thumbObjectMetadata.setContentType(image.getContentType());

            // save in s3
            InputStream thumbInput = new ByteArrayInputStream(thumbBytes);
            String thumbnailStoreName = "s_" + UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

            amazonS3Client.putObject(
                    new PutObjectRequest(bucket,thumbnailStoreName,thumbInput,thumbObjectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String thumbnailStoreUrl = amazonS3Client.getUrl(bucket, thumbnailStoreName).toString();

            user.changeThumbnailImage(thumbnailStoreUrl, thumbnailStoreName);

            thumbInput.close();
            thumbOutPut.close();

        } catch (Exception e) {
            throw new UserException(e.getMessage());
        }

    }


    private void validateFileType(MultipartFile file) {
        // TODO TIKA 적용
        if (file.getContentType().startsWith("image") == false) {
            throw new UserException("이미지의 파일타입이 잘못되었습니다.");
        }
    }
}
