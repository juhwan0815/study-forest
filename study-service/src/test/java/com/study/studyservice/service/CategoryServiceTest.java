package com.study.studyservice.service;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.model.category.request.CategorySaveRequest;
import com.study.studyservice.model.category.request.CategoryUpdateRequest;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.repository.CategoryRepository;
import com.study.studyservice.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory(){
        // given
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest();
        categoryUpdateRequest.setName("프론트엔드");

        Category category = Category.createCategory("백엔드", null);

        given(categoryRepository.findByName(anyString()))
                .willReturn(Optional.empty());

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(category));

        // when
        CategoryResponse result = categoryService.update(1L, categoryUpdateRequest);

        // then
        assertThat(result.getName()).isEqualTo(categoryUpdateRequest.getName());
        then(categoryRepository).should(times(1)).findByName(any());
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("카테고리 수정 - 실패")
    void updateDuplicatedCategory() {
        // given
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest();
        categoryUpdateRequest.setName("프론트엔드");

        Category category = Category.createCategory("프론트엔드", null);

        given(categoryRepository.findByName(anyString()))
                .willReturn(Optional.of(category));

        // when
        assertThrows(CategoryException.class,()->categoryService.update(1L,categoryUpdateRequest));
    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory(){
        Category category = Category.createCategory("프론트엔드", null);

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(category));

        categoryService.delete(1L);

        assertThat(category.getStatus()).isEqualTo(CategoryStatus.DELETE);
    }

    @Test
    @DisplayName("부모 카테고리 조회")
    void findParentCategory(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        List<Category> parentList = new ArrayList<>();
        parentList.add(parentCategory);

        given(categoryRepository.findByParentIsNull())
                .willReturn(parentList);

        // when
        List<CategoryResponse> result = categoryService.findParent();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(parentCategory.getName());
        then(categoryRepository).should(times(1)).findByParentIsNull();
    }

    @Test
    @DisplayName("자식 카테고리 조회")
    void findChildCategory(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        List<Category> childList = new ArrayList<>();
        childList.add(childCategory);

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(parentCategory));

        given(categoryRepository.findByParent(any()))
                .willReturn(childList);

        // when
        List<CategoryResponse> result = categoryService.findChild(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(childCategory.getName());
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByParent(any());
    }
}