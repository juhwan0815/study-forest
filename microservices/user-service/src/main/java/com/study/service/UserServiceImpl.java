package com.study.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.client.KakaoClient;
import com.study.client.KakaoClientImpl;
import com.study.domain.Image;
import com.study.domain.User;
import com.study.domain.UserRole;
import com.study.dto.KakaoProfile;
import com.study.dto.UserResponse;
import com.study.dto.UserUpdateRequest;
import com.study.repository.UserQueryRepository;
import com.study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final AmazonS3Client amazonS3Client;
    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    @Transactional
    public UserResponse create(String kakaoToken) {
        KakaoProfile kakaoProfile = kakaoClient.getKakaoProfile(kakaoToken);

        if (userRepository.existsByKakaoId(kakaoProfile.getId())) {
            throw new RuntimeException("");
        }

        User user = User.createUser(kakaoProfile.getId(),
                kakaoProfile.getProperties().getNickname(),
                kakaoProfile.getKakao_account().getGender(),
                kakaoProfile.getKakao_account().getAge_range(), UserRole.USER);
        user.changeImage(Image.createImage(kakaoProfile.getProperties().getThumbnail_image(), null));
        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse findByKakaoId(Long kakaoId, String fcmToken) {
        User findUser = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException(""));

        findUser.changeFcmToken(fcmToken);
        return UserResponse.from(findUser);
    }

    @Override
    public UserResponse findById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException());
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public UserResponse updateImage(Long userId, MultipartFile image) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        Image updateImage = uploadImage(image, findUser.getImage().getImageStoreName());
        findUser.changeImage(updateImage);
        return UserResponse.from(findUser);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));

        findUser.changeProfile(request.getNickName());
        return UserResponse.from(findUser);
    }

    private Image uploadImage(MultipartFile image, String imageStoreName) {

        Image returnImage = null;

        if (image == null) {
            deleteImageFromS3(imageStoreName);
        } else {
            if (!image.isEmpty()) {
                deleteImageFromS3(imageStoreName);
                validateImageType(image);
                returnImage = uploadImageToS3(image);
            }
        }
        return returnImage;
    }

    private void deleteImageFromS3(String imageStoreName) {
        if (imageStoreName == null) {
            amazonS3Client.deleteObject(bucket, imageStoreName);
        }
    }

    private void validateImageType(MultipartFile image) {
        if (!image.getContentType().startsWith("image")) {
            throw new RuntimeException("이미지의 파일타입이 잘못되었습니다.");
        }
    }

    private Image uploadImageToS3(MultipartFile image) {
        String ext = extractExt(image.getContentType());
        String imageStoreName = UUID.randomUUID().toString() + "." + ext;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        Image uploadResult = null;
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, imageStoreName, image.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
            String imageUrl = amazonS3Client.getUrl(bucket, imageStoreName).toString();
            uploadResult = Image.createImage(imageUrl, imageStoreName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return uploadResult;
    }

    private String extractExt(String contentType) {
        int pos = contentType.lastIndexOf("/");
        return contentType.substring(pos + 1);
    }
}
