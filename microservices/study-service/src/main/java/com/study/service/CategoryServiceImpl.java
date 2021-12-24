package com.study.service;


import com.study.domain.Category;
import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.dto.category.CategoryUpdateRequest;
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
            throw new RuntimeException();
        }

        Category category = Category.createCategory(request.getName(), null);
        categoryRepository.save(category);

        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public CategoryResponse createChildren(Long categoryId, CategoryCreateRequest request) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException());

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException();
        }

        Category category = Category.createCategory(request.getName(), findCategory);
        categoryRepository.save(category);

        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long categoryId, CategoryUpdateRequest request) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException());

        findCategory.changeName(request.getName());

        return CategoryResponse.from(findCategory);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {

        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException());

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
                .orElseThrow(() -> new RuntimeException());

        return findCategory.getChildren().stream()
                .map(category -> CategoryResponse.from(category))
                .collect(Collectors.toList());
    }
}
