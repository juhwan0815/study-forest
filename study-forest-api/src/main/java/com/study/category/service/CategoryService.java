package com.study.category.service;

import com.study.category.Category;
import com.study.category.CategoryRepository;
import com.study.category.dto.CategoryCreateRequest;
import com.study.common.DuplicateException;
import com.study.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.common.NotFoundException.CATEGORY_NOT_FOUND;

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

    @Transactional
    public Long createChildren(Long categoryId, CategoryCreateRequest request) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 학과입니다.", request.getName()));
        }

        Category category = Category.createCategory(request.getName(), findCategory);
        return categoryRepository.save(category).getId();
    }
}
