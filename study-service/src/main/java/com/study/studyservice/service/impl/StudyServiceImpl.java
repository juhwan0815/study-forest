package com.study.studyservice.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.studyservice.client.LocationServiceClient;
import com.study.studyservice.domain.*;
import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.exception.StudyException;
import com.study.studyservice.model.image.ImageUploadResult;
import com.study.studyservice.model.location.response.LocationResponse;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.repository.StudyRepository;
import com.study.studyservice.repository.StudyUserRepository;
import com.study.studyservice.repository.TagRepository;
import com.study.studyservice.repository.query.StudyQueryRepository;
import com.study.studyservice.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final TagRepository tagRepository;
    private final StudyRepository studyRepository;
    private final LocationServiceClient locationServiceClient;
    private final CategoryRepository categoryRepository;
    private final AmazonS3Client amazonS3Client;
    private final StudyQueryRepository studyQueryRepository;
    private final StudyUserRepository studyUserRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

    @Override
    @Transactional
    public StudyResponse create(Long userId,MultipartFile image,StudyCreateRequest request) {
        Category findCategory = categoryRepository.findWithParentById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(request.getCategoryId() + "는 존재하지 않는 카테고리 ID입니다."));

        LocationResponse location = getLocation(request.isOffline(),request.getLocationCode());

        ImageUploadResult imageUploadResult = uploadImage(image);

        List<Tag> tagList = tagRepository.findByNameIn(request.getTags());

        searchNewTags(request.getTags(), tagList);

        StudyUser studyUser = StudyUser.createStudyUser(userId, Role.ADMIN);

        List<StudyTag> studyTags = tagList.stream()
                .map(tag -> StudyTag.createStudyTag(tag))
                .collect(Collectors.toList());

        Study study = Study.createStudy(request.getName(),
                request.getNumberOfPeople(),
                request.getContent(),
                request.isOnline(), request.isOffline(),
                imageUploadResult.getImageStoreName(),
                imageUploadResult.getStudyImage(),
                imageUploadResult.getStudyThumbnailImage(),
                location.getId(),
                findCategory,
                studyUser,
                studyTags);

        Study savedStudy = studyRepository.save(study);

        return StudyResponse.from(savedStudy, location);
    }

    @Override
    @Transactional
    public StudyResponse update(Long userId, Long studyId, MultipartFile image, StudyUpdateRequest request) {
        Study findStudy = studyQueryRepository.findWithStudyTagsAndTagById(studyId);

        StudyUser findStudyAdminUser = studyUserRepository
                .findByUserIdAndAndRoleAndStudy(userId, Role.ADMIN, findStudy)
                .orElseThrow(() -> new StudyException("스터디를 수정할 권한이 없습니다."));

        Category findCategory = categoryRepository.findWithParentById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(request.getCategoryId() + "는 존재하지 않는 카테고리입니다."));

        LocationResponse location = getLocation(request.isOffline(), request.getLocationCode());

        List<Tag> tagList = tagRepository.findByNameIn(request.getTags());
        searchNewTags(request.getTags(),tagList);

        if(request.isDeleteImage()){
            deleteImageFromS3(findStudy.getImageStoreName());
            findStudy.deleteImage();
        }
        if(!image.isEmpty()){
            validateImageType(image);
            deleteImageFromS3(findStudy.getImageStoreName());
            ImageUploadResult imageUploadResult = uploadImage(image);

            findStudy.changeImage(imageUploadResult.getStudyImage(),
                                  imageUploadResult.getStudyThumbnailImage(),
                                  imageUploadResult.getImageStoreName());
        }


        findStudy.update(request.getName(),
                request.getNumberOfPeople(),
                request.getContent(),
                request.isOnline(),
                request.isOffline(),
                request.isClose(),
                location.getId(),
                findCategory,
                tagList);

        return StudyResponse.from(findStudy,location);
    }

    private LocationResponse getLocation(boolean offline,String locationCode) {
        LocationResponse location;
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

    private ImageUploadResult uploadImage(MultipartFile image) {
        ImageUploadResult imageUploadResult;

        if (image.isEmpty()) {
            imageUploadResult = ImageUploadResult.from(null, null, null);
        } else {
            validateImageType(image);
            imageUploadResult = uploadImageFromS3(image);
        }
        return imageUploadResult;
    }

    private ImageUploadResult uploadImageFromS3(MultipartFile image) {
        String imageStoreName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        ImageUploadResult imageUploadResult = null;

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, imageStoreName,
                    image.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String profileImage = amazonS3Client.getUrl(bucket, imageStoreName).toString();
            String thumbnailImage = amazonS3Client.getUrl(thumbnailBucket, imageStoreName).toString();

            imageUploadResult = ImageUploadResult.from(profileImage, thumbnailImage, imageStoreName);

        } catch (Exception e) {
            throw new StudyException(e.getMessage());
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
        // TODO TIKA 적용
        if (image.getContentType().startsWith("image") == false) {
            throw new StudyException("이미지의 파일타입이 잘못되었습니다.");
        }
    }

    private void searchNewTags(List<String> requestTags, List<Tag> tagList) {
        requestTags
                .forEach(name -> {
                    boolean matchResult = false; // false 로 설정

                    // DB의 태그이름과 비교하고 일치하면 true
                    for (Tag tag : tagList) {
                        if (name.equals(tag.getName())) {
                            matchResult = true;
                            break;
                        }
                    }

                    // true일 경우 DB에 존재
                    // false일 경우 DB에 없는 태그이므로 생성하고 저장
                    if (matchResult == false) {
                        Tag savedTag = tagRepository.save(Tag.createTag(name));
                        tagList.add(savedTag);
                    }
                });
    }
}
