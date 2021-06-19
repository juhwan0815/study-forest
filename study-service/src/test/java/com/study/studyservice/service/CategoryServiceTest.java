package com.study.studyservice.service;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import com.study.studyservice.exception.CategoryException;
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

import static com.study.studyservice.fixture.CategoryFixture.*;
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
    @DisplayName("부모 카테고리를 생성한다.")
    void createParentCategory(){
        // given
        given(categoryRepository.findByNameAndStatus(anyString(),any()))
                .willReturn(Optional.empty());

        given(categoryRepository.save(any()))
                .willReturn(TEST_CATEGORY1);

        // when
        CategoryResponse result = categoryService.save(TEST_CATEGORY_SAVE_REQUEST1);

        // then
        assertThat(result.getId()).isEqualTo(TEST_CATEGORY1.getId());
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_SAVE_REQUEST1.getName());
        then(categoryRepository).should(times(1)).findByNameAndStatus(any(),any());
        then(categoryRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("자식 카테고리를 생성한다.")
    void createChildCategory(){
        // given
        given(categoryRepository.findByNameAndStatus(anyString(),any()))
                .willReturn(Optional.empty());

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY1));

        given(categoryRepository.save(any()))
                .willReturn(TEST_CATEGORY2);

        // when
        CategoryResponse result = categoryService.save(TEST_CATEGORY_SAVE_REQUEST2);

        // then
        assertThat(result.getId()).isEqualTo(TEST_CATEGORY2.getId());
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_SAVE_REQUEST2.getName());
        then(categoryRepository).should(times(1)).findByNameAndStatus(any(),any());
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외테스트 : 중복된 이름의 카테고리를 생성할 경우 예외가 발생한다.")
    void createDuplicatedNameCategory(){
        // given
        given(categoryRepository.findByNameAndStatus(anyString(),any()))
                .willReturn(Optional.of(TEST_CATEGORY1));

        // when
        assertThrows(CategoryException.class,()-> categoryService.save(TEST_CATEGORY_SAVE_REQUEST1));
    }

    @Test
    @DisplayName("카테고리의 이름을 수정한다.")
    void updateCategory(){
        // given
        Category category = createTestCategory();

        given(categoryRepository.findByNameAndStatus(anyString(),any()))
                .willReturn(Optional.empty());

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(category));

        // when
        CategoryResponse result = categoryService.update(1L, TEST_CATEGORY_UPDATE_REQUEST);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_UPDATE_REQUEST.getName());

        then(categoryRepository).should(times(1)).findByNameAndStatus(any(),any());
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("예외테스트 : 중복된 이름의 카테고리로 변경할 경우 예외가 발생한다.")
    void updateDuplicatedNameCategory() {
        // given
        given(categoryRepository.findByNameAndStatus(anyString(),any()))
                .willReturn(Optional.of(TEST_CATEGORY1));

        // when
        assertThrows(CategoryException.class,()->categoryService.update(1L,TEST_CATEGORY_UPDATE_REQUEST));
    }

    @Test
    @DisplayName("카테고리의 상태를 삭제 상태로 변경한다.")
    void deleteCategory(){
        // given
        Category category = createTestCategory();

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(category));

        categoryService.delete(1L);

        assertThat(category.getStatus()).isEqualTo(CategoryStatus.DELETE);
    }

    @Test
    @DisplayName("부모 카테고리 조회")
    void findParentCategory(){
        // given
        List<Category> parentList = new ArrayList<>();
        parentList.add(TEST_CATEGORY1);

        given(categoryRepository.findByParentIsNullAndStatus(any()))
                .willReturn(parentList);

        // when
        List<CategoryResponse> result = categoryService.findParent();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(TEST_CATEGORY1.getName());

        then(categoryRepository).should(times(1)).findByParentIsNullAndStatus((any()));
    }

    @Test
    @DisplayName("자식 카테고리 조회")
    void findChildCategory(){
        // given
        List<Category> childList = new ArrayList<>();
        childList.add(TEST_CATEGORY2);

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY1));

        given(categoryRepository.findByParentAndStatus(any(),any()))
                .willReturn(childList);

        // when
        List<CategoryResponse> result = categoryService.findChild(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(TEST_CATEGORY2.getName());

        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByParentAndStatus(any(),any());
    }
}