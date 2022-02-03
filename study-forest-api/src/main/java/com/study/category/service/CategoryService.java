package com.study.category.service;

import com.study.category.Category;
import com.study.category.CategoryRepository;
import com.study.category.dto.CategoryCreateRequest;
import com.study.common.DuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long createParent(CategoryCreateRequest request) {

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 카테고리입니다.", request.getName()));
        }

        Category category = Category.createCategory(request.getName(), null);
        return categoryRepository.save(category).getId();
    }
}
