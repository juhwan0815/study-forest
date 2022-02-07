package com.study.service;


import com.study.domain.Category;
import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;
import com.study.exception.DuplicateException;
import com.study.exception.NotFoundException;
import com.study.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.exception.NotFoundException.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Long create(CategoryCreateRequest request) {

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 카테고리입니다.", request.getName()));
        }

        Category category = Category.createCategory(request.getName(), null);
        return categoryRepository.save(category).getId();
    }

    @Override
    @Transactional
    public Long createChildren(Long categoryId, CategoryCreateRequest request) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 카테고리입니다.", request.getName()));
        }

        Category category = Category.createCategory(request.getName(), findCategory);
        return categoryRepository.save(category).getId();
    }

    @Override
    @Transactional
    public void update(Long categoryId, CategoryUpdateRequest request) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 카테고리입니다.", request.getName()));
        }

        findCategory.changeName(request.getName());
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        categoryRepository.delete(findCategory);
    }

    @Override
    public List<CategoryResponse> findParent() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> findByParent(Long categoryId) {
        Category findCategory = categoryRepository.findWithChildrenById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));

        return findCategory.getChildren().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }
}
