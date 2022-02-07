package com.study.service;

import com.study.domain.Category;
import com.study.dto.category.CategoryResponse;
import com.study.exception.DuplicateException;
import com.study.exception.NotFoundException;
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

import static com.study.CategoryFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
                .willReturn(TEST_CATEGORY);

        // when
        Long result = categoryService.create(TEST_CATEGORY_CREATE_REQUEST);

        // then
        assertThat(result).isEqualTo(TEST_CATEGORY.getId());
        then(categoryRepository).should(times(1)).findByName(any());
        then(categoryRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("중복된 카테고리를 생성하면 예외가 발생한다.")
    void createDuplicate() {
        // given
        given(categoryRepository.findByName(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        assertThrows(DuplicateException.class, () -> categoryService.create(TEST_CATEGORY_CREATE_REQUEST));

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
                .willReturn(TEST_CATEGORY);

        // when
        Long result = categoryService.createChildren(TEST_CATEGORY.getId(), TEST_CATEGORY_CREATE_REQUEST);

        // then
        assertThat(result).isEqualTo(TEST_CATEGORY.getId());
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
        then(categoryRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("하위 카테고리를 생성할 때 상위 카테고리가 존재하지 않으면 예외가 발생한다.")
    void createChildrenNotFound() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> categoryService.createChildren(TEST_CATEGORY.getId(), TEST_CATEGORY_CREATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("중복된 하위 카테고리를 생성하면 예외가 발생한다.")
    void createChildrenDuplicate() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        assertThrows(DuplicateException.class, () -> categoryService.createChildren(TEST_CATEGORY.getId(), TEST_CATEGORY_CREATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("카테고리를 수정한다.")
    void update() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.empty());

        // when
        categoryService.update(TEST_CATEGORY.getId(), TEST_CATEGORY_UPDATE_REQUEST);

        // then
        then(categoryRepository).should(times(1)).findById(any());
        then(categoryRepository).should(times(1)).findByName(any());
    }

    @Test
    @DisplayName("카테고리를 수정할 때 카테고리가 존재하지 않으면 예외가 발생한다.")
    void updateNotFound() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> categoryService.update(TEST_CATEGORY.getId(), TEST_CATEGORY_UPDATE_REQUEST));

        // then
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("중복된 카테고리로 변경하면 예외가 발생한다.")
    void updateDuplicate() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        given(categoryRepository.findByName(any()))
                .willReturn(Optional.of(TEST_CATEGORY));

        // when
        assertThrows(DuplicateException.class, () -> categoryService.update(TEST_CATEGORY.getId(), TEST_CATEGORY_UPDATE_REQUEST));

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
        categoryService.delete(TEST_CATEGORY.getId());

        // then
        then(categoryRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("카테고리를 삭제할 때 카테고리가 존재하지 않으면 예외가 발생한다.")
    void deleteNotFound() {
        // given
        given(categoryRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> categoryService.delete(TEST_CATEGORY.getId()));

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
        Category category = new Category(1L, TEST_CATEGORY_NAME, null, Arrays.asList(TEST_CATEGORY));

        given(categoryRepository.findWithChildrenById(any()))
                .willReturn(Optional.of(category));

        // when
        List<CategoryResponse> result = categoryService.findByParent(category.getId());

        // then
        assertThat(result.size()).isEqualTo(1);
        then(categoryRepository).should(times(1)).findWithChildrenById(any());
    }

    @Test
    @DisplayName("하위 카테고리를 조회할 때 상위 카테고리가 존재하지 않으면 예외가 발생한다.")
    void findByParentNotFound() {
        // given
        given(categoryRepository.findWithChildrenById(any()))
                .willReturn(Optional.empty());

        // when
        assertThrows(NotFoundException.class, () -> categoryService.findByParent(TEST_CATEGORY.getId()));

        // then
        then(categoryRepository).should(times(1)).findWithChildrenById(any());
    }

}