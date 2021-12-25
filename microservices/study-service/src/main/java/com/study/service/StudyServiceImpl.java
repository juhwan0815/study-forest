package com.study.service;

import com.study.client.AreaResponse;
import com.study.client.AreaServiceClient;
import com.study.domain.Category;
import com.study.domain.Image;
import com.study.domain.Study;
import com.study.domain.StudyRole;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudyUpdateAreaRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.kakfa.StudyCreateMessage;
import com.study.kakfa.sender.StudyCreateMessageSender;
import com.study.repository.CategoryRepository;
import com.study.repository.StudyQueryRepository;
import com.study.repository.StudyRepository;
import com.study.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final StudyQueryRepository studyQueryRepository;
    private final CategoryRepository categoryRepository;
    private final AreaServiceClient areaServiceClient;
    private final ImageUtil imageUtil;

    private final StudyCreateMessageSender studyCreateMessageSender;

    @Override
    @Transactional
    public StudyResponse create(Long userId, MultipartFile file, StudyCreateRequest request) {
        Category findCategory = categoryRepository.findWithParentById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException());

        AreaResponse area = new AreaResponse();
        if (request.getOffline() == true) {
            area = areaServiceClient.findByCode(request.getAreaCode());
        }

        Image image = imageUtil.uploadImage(file, null);

        Study study = Study.createStudy(request.getName(), request.getContent(), request.getNumberOfPeople(),
                request.getOnline(), request.getOffline(), findCategory);
        study.addStudyUser(userId, StudyRole.ADMIN);
        study.changeImage(image);
        study.addTags(request.getTags());
        study.changeArea(area.getId());
        studyRepository.save(study);

        studyCreateMessageSender.send(new StudyCreateMessage(study.getId(), study.getName(), request.getTags()));
        return StudyResponse.from(study, area);
    }

    @Override
    @Transactional
    public StudyResponse updateImage(Long userId, Long studyId, MultipartFile file) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new RuntimeException());
        findStudy.isStudyAdmin(userId);

        Image image = imageUtil.uploadImage(file, findStudy.getImage());
        findStudy.changeImage(image);
        return StudyResponse.from(findStudy);
    }

    @Override
    @Transactional
    public StudyResponse updateArea(Long userId, Long studyId, StudyUpdateAreaRequest request) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new RuntimeException());
        findStudy.isStudyAdmin(userId);

        AreaResponse area = areaServiceClient.findByCode(request.getAreaCode());
        findStudy.changeArea(area.getId());
        return StudyResponse.from(findStudy, area);
    }

    @Override
    @Transactional
    public StudyResponse update(Long userId, Long studyId, StudyUpdateRequest request) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new RuntimeException());
        findStudy.isStudyAdmin(userId);

        Category findCategory = categoryRepository.findWithParentById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException());

        findStudy.change(request.getName(), request.getContent(), request.getNumberOfPeople(),
                         request.getOnline(), request.getOffline(), request.getOpen(), findCategory);

        return StudyResponse.from(findStudy);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long studyId) {
        Study findStudy = studyRepository.findWithStudyUserById(studyId)
                .orElseThrow(() -> new RuntimeException());
        findStudy.isStudyAdmin(userId);

        studyRepository.delete(findStudy);
    }
}
