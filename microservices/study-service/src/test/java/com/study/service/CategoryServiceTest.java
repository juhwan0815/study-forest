package com.study.service;

import com.study.domain.Category;
import com.study.dto.category.CategoryCreateRequest;
import com.study.dto.category.CategoryResponse;
import com.study.fixture.CategoryFixture;
import com.study.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.study.fixture.CategoryFixture.*;
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
    @DisplayName("카테고리를 생성한다.")
    void create() {
        // given
        given(categoryRepository.findByName(any()))
                .willReturn(Optional.empty());

        given(categoryRepository.save(any()))
                .willReturn(null);

        // when
        CategoryResponse result = categoryService.create(TEST_CATEGORY_CREATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_CREATE_REQUEST.getName());
        then(categoryRepository).should(times(1)).findByName(any());
        then(categoryRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외 테스트 : 중복된 이름의 카테고리를 생성하면 예외가 발생한다.")
    void createDuplicateName() {
        // given
        given(categoryRepository.findByName(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        assertThrows(RuntimeException.class, () -> categoryService.create(TEST_CATEGORY_CREATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("하위 카테고리를 생성한다.")
    void createChildren() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.empty());

        given(categoryRepository.save(any()))
                .willReturn(null);

        // when
        CategoryResponse result = categoryService.createChildren(1L, TEST_CATEGORY_CREATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_CREATE_REQUEST.getName());
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
        then(categoryRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("예외 테스트 : 중복된 이름의 하위 카테고리를 생성하면 예외가 발생한다.")
    void createDuplicateChildren() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.of(TEST_CHILD_CATEGORY));

        // when
        assertThrows(RuntimeException.class, () -> categoryService.createChildren(1L, TEST_CATEGORY_CREATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("카테로리의 이름을 변경한다.")
    void update() {
        // given
        Category category = Category.createCategory("개발", null);

        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(category));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.empty());

        // when
        CategoryResponse result = categoryService.update(1L, TEST_CATEGORY_UPDATE_REQUEST);

        // then
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_UPDATE_REQUEST.getName());
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("예외 테스트 : 중복된 이름으로 카테고리를 변경하면 예외가 발생한다.")
    void updateDuplicateName() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.of(TEST_CHILD_CATEGORY));

        // when
        assertThrows(RuntimeException.class, () -> categoryService.update(1L, TEST_CATEGORY_UPDATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    void delete() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        categoryService.delete(1L);

        // then
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("상위 카테고리를 조회한다.")
    void findParent() {
        // given
        given(categoryRepository.findByParentIsNull())
                .willReturn(Arrays.asList(TEST_CATEGORY));

        // when
        List<CategoryResponse> result = categoryService.findParent();

        // then
        assertThat(result.size()).isEqualTo(1);
        then(categoryRepository).should(times(1)).findByParentIsNull();
    }

    @Test
    @DisplayName("하위 카테고리를 조회한다.")
    void findByParent() {
        // given
        given(categoryRepository.findWithChildrenById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        List<CategoryResponse> result = categoryService.findByParent(1L);

        // then
        assertThat(result.size()).isEqualTo(1);
        then(categoryRepository).should(times(1)).findWithChildrenById(any());
    }

}