package com.study.study.service;

import com.study.area.Area;
import com.study.area.AreaRepository;
import com.study.category.Category;
import com.study.category.CategoryRepository;
import com.study.common.NotFoundException;
import com.study.study.Study;
import com.study.study.StudyRepository;
import com.study.study.dto.StudyCreateRequest;
import com.study.studyuser.StudyRole;
import com.study.studyuser.StudyUser;
import com.study.studyuser.StudyUserRepository;
import com.study.user.User;
import com.study.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Long create(Long userId, StudyCreateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Category findCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        Area findArea = null;
        if (request.getCode() == null) {
            findArea = areaRepository.findByCode(request.getCode())
                    .orElseThrow(() -> new NotFoundException(AREA_NOT_FOUND));
        }

        Study study = Study.createStudy(request.getName(), request.getContent(), request.getNumberOfPeople(),
                                        request.getOnline(), request.getOffline(), findCategory, findArea);
        study.changeImageUrl(request.getImageUrl());
        study.changeTags(request.getTags());
        Study savedStudy = studyRepository.save(study);

        StudyUser studyUser = StudyUser.createStudyUser(StudyRole.ADMIN, findUser, study);
        studyUserRepository.save(studyUser);
        return savedStudy.getId();
    }
}
