package com.study.studyservice.repository;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 저장")
    void save(){
        // given
        Category category = Category.createCategory("토익", null);

        // when
        Category savedCategory = categoryRepository.save(category);

        // then
        assertThat(savedCategory.getId()).isNotNull();
    }

    @Test
    @DisplayName("카테고리ID로 조회")
    void findById(){
        // given
        Category category = Category.createCategory("토익", null);

        Category savedCategory = categoryRepository.save(category);


        // when
        Category findCategory = categoryRepository.findById(savedCategory.getId()).get();

        assertThat(findCategory.getId()).isEqualTo(savedCategory.getId());
        assertThat(findCategory.getName()).isEqualTo(savedCategory.getName());
    }

    @Test
    @DisplayName("카테고리 이름으로 조회")
    void findByName(){
        // given
        Category category = Category.createCategory("토익", null);

        Category savedCategory = categoryRepository.save(category);

        // when
        Category findCategory = categoryRepository.findByName("토익").get();

        // then
        assertThat(findCategory.getId()).isEqualTo(savedCategory.getId());
        assertThat(findCategory.getName()).isEqualTo(savedCategory.getName());
    }

    @Test
    @DisplayName("부모 카테고리 조회")
    void findByParentIsNull(){
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        Category savedParentCategory = categoryRepository.save(parentCategory);
        Category savedChildCategory = categoryRepository.save(childCategory);

        List<Category> result = categoryRepository.findByParentIsNullAndStatus(CategoryStatus.ACTIVE);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(savedParentCategory.getId());
    }

    @Test
    @DisplayName("자식 카테고리 조회")
    void findByParent(){
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        Category savedParentCategory = categoryRepository.save(parentCategory);
        Category savedChildCategory = categoryRepository.save(childCategory);

        List<Category> result = categoryRepository.findByParentAndStatus(savedParentCategory,CategoryStatus.ACTIVE);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(savedChildCategory.getId());
    }

    @Test
    @DisplayName("카테고리 (부모포함) 조회")
    void findWithParentById(){
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        Category savedParentCategory = categoryRepository.save(parentCategory);
        Category savedChildCategory = categoryRepository.save(childCategory);

        Category result = categoryRepository.findWithParentById(savedChildCategory.getId()).get();

        assertThat(result.getId()).isEqualTo(savedChildCategory.getId());
        assertThat(result.getName()).isEqualTo(savedChildCategory.getName());
        assertThat(result.getParent().getId()).isEqualTo(savedParentCategory.getId());
        assertThat(result.getParent().getName()).isEqualTo(savedParentCategory.getName());
    }


}