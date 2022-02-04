package com.study.study.service;

import com.study.area.Area;
import com.study.area.AreaRepository;
import com.study.category.Category;
import com.study.category.CategoryRepository;
import com.study.client.AwsClient;
import com.study.common.NotFoundException;
import com.study.study.Study;
import com.study.study.StudyRepository;
import com.study.study.dto.StudyCreateRequest;
import com.study.study.dto.StudyUpdateRequest;
import com.study.studyuser.StudyRole;
import com.study.studyuser.StudyUser;
import com.study.studyuser.StudyUserRepository;
import com.study.user.User;
import com.study.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.study.common.NotFoundException.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;
    private final StudyUserRepository studyUserRepository;

    private final AwsClient awsClient;

    public String uploadImage(MultipartFile image) {
        return awsClient.upload(image);
    }

    @Transactional
    public Long create(Long userId, StudyCreateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Category findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        Area findArea = null;
        if (request.getCode() != null) {
            findArea = areaRepository.findByCode(request.getCode())
                    .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));
        }

        Study study = Study.createStudy(request.getName(), request.getContent(), request.getNumberOfPeople(),
                request.getOnline(), request.getOffline(), request.getImageUrl(), findCategory, findArea);
        study.changeTags(request.getTags());
        Study saveStudy = studyRepository.save(study);

        StudyUser studyUser = StudyUser.createStudyUser(StudyRole.ADMIN, findUser, study);
        studyUserRepository.save(studyUser);
        return saveStudy.getId();
    }

    @Transactional
    public void update(Long userId, Long studyId, StudyUpdateRequest request) {
        Study findStudy = studyRepository.findWithTagById(studyId)
                .orElseThrow(() -> new NotFoundException(STUDY_NOT_FOUND));

        if (studyUserRepository.findByUserIdAndAndAndStudyIdAndStudyRole(userId, studyId, StudyRole.ADMIN).isPresent()) {
            throw new AccessDeniedException("");
        }

        Category findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        Area findArea = null;
        if (request.getCode() != null) {
            findArea = areaRepository.findByCode(request.getCode())
                    .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));
        }

        findStudy.change(request.getName(), request.getContent(), request.getNumberOfPeople(), request.getOnline(),
                request.getOffline(), request.getOpen(), request.getImageUrl(), findCategory, findArea);
        findStudy.changeTags(request.getTags());
    }

    @Transactional
    public void delete(Long userId, Long studyId) {
        if (studyUserRepository.findByUserIdAndAndAndStudyIdAndStudyRole(userId, studyId, StudyRole.ADMIN).isPresent()) {
            throw new AccessDeniedException("");
        }

        // 삭제
    }
}
