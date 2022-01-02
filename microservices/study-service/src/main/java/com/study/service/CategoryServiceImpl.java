package com.study.service;


import com.study.domain.Category;
import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;
import com.study.exception.CategoryDuplicateException;
import com.study.exception.CategoryNotFoundException;
import com.study.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CategoryDuplicateException(request.getName() + "는 이미 존재하는 카테고리입니다.");
        }

        Category category = Category.createCategory(request.getName(), null);
        categoryRepository.save(category);

        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public CategoryResponse createChildren(Long categoryId, CategoryCreateRequest request) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId + "는 존재하지 않는 카테고리 ID 입니다."));

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CategoryDuplicateException(request.getName() + "는 이미 존재하는 카테고리입니다.");
        }

        Category category = Category.createCategory(request.getName(), findCategory);
        categoryRepository.save(category);

        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long categoryId, CategoryUpdateRequest request) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId + "는 존재하지 않는 카테고리 ID 입니다."));

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CategoryDuplicateException(request.getName() + "는 이미 존재하는 카테고리입니다.");
        }

        findCategory.changeName(request.getName());

        return CategoryResponse.from(findCategory);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId + "는 존재하지 않는 카테고리 ID 입니다."));

        categoryRepository.delete(findCategory);
    }

    @Override
    public List<CategoryResponse> findParent() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(category -> CategoryResponse.from(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> findByParent(Long categoryId) {
        Category findCategory = categoryRepository.findWithChildrenById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId + "는 존재하지 않는 카테고리 ID 입니다."));

        return findCategory.getChildren().stream()
                .map(category -> CategoryResponse.from(category))
                .collect(Collectors.toList());
    }
}
