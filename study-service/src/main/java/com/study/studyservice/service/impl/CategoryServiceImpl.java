package com.study.studyservice.service.impl;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.request.CategoryUpdateRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse save(CategorySaveRequest request) {

        Category parent = null;

        if(categoryRepository.findByName(request.getName()).isPresent()){
            throw new CategoryException(request.getName() + "은 이미 존재하는 카테고리입니다.");
        }

        if(request.getId() != null){
            parent = categoryRepository.findById(request.getId())
                    .orElseThrow(()-> new CategoryException(request.getId() + "은 존재하지 않는 카테고리 ID입니다."));
        }

        Category category = Category.createCategory(request.getName(), parent);
        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.from(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long categoryId,CategoryUpdateRequest request) {

        if(categoryRepository.findByName(request.getName()).isPresent()){
            throw new CategoryException(request.getName() + "은 이미 존재하는 카테고리입니다.");
        }

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(categoryId + "은 존재하지 않는 카테고리 ID입니다."));

        findCategory.changeName(request.getName());

        return CategoryResponse.from(findCategory);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(categoryId + "은 존재하지 않는 카테고리 ID입니다."));

        findCategory.delete();
    }

    @Override
    public List<CategoryResponse> findParent() {
        return categoryRepository.findByParentIsNullAndStatus(CategoryStatus.ACTIVE)
                .stream().map(category -> CategoryResponse.from(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> findChild(Long categoryId) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(categoryId + "는 존재하지 않는 카테고리 ID입니다."));

        return categoryRepository.findByParentAndStatus(findCategory,CategoryStatus.ACTIVE)
                .stream()
                .map(category -> CategoryResponse.from(category))
                .collect(Collectors.toList());
    }


}
