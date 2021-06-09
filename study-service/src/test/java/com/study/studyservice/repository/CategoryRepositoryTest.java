package com.study.studyservice.repository;

import com.study.studyservice.domain.Category;
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

        List<Category> result = categoryRepository.findByParentIsNull();

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

        List<Category> result = categoryRepository.findByParent(savedParentCategory);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(savedChildCategory.getId());
    }
}