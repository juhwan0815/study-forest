package com.study.studyservice.service.impl;

import com.study.studyservice.domain.Category;
import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
