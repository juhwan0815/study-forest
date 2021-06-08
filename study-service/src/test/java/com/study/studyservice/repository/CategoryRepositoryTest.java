package com.study.studyservice.repository;

import com.study.studyservice.domain.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
}