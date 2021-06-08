package com.study.studyservice.service;

import com.study.studyservice.domain.Category;
import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory(){
        // given
        CategorySaveRequest categorySaveRequest = new CategorySaveRequest();
        categorySaveRequest.setId(1L);
        categorySaveRequest.setName("백엔드");

        Category category = Category.createCategory("개발", null);
        Category childParent = Category.createCategory("백엔드", category);

        given(categoryRepository.findByName(anyString()))
                .willReturn(Optional.empty());

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(category));

        given(categoryRepository.save(any()))
                .willReturn(childParent);

        // when
        CategoryResponse result = categoryService.save(categorySaveRequest);

        // then
        assertThat(result.getName()).isEqualTo(childParent.getName());
        then(categoryRepository).should(times(1)).save(any());
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("카테고리 생성 - 실패")
    void createDuplicatedCategory(){
        // given
        CategorySaveRequest categorySaveRequest = new CategorySaveRequest();
        categorySaveRequest.setId(1L);
        categorySaveRequest.setName("백엔드");

        Category category = Category.createCategory("백엔드", null);

        given(categoryRepository.findByName(anyString()))
                .willReturn(Optional.of(category));

        // when
        assertThrows(CategoryException.class,()-> categoryService.save(categorySaveRequest));
    }

}