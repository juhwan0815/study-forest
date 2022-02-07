package com.study.repository;

import com.study.domain.Category;
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
    @DisplayName("이름으로 카테고리를 조회한다.")
    void findByName() {
        // given
        Category category = Category.createCategory("백엔드", null);
        categoryRepository.save(category);

        // when
        Category result = categoryRepository.findByName("백엔드").get();

        // then
        assertThat(result).isEqualTo(category);
    }

    @Test
    @DisplayName("상위 카테고리를 조회한다.")
    void findByParentIsNull() {
        // given
        Category category = Category.createCategory("백엔드", null);
        categoryRepository.save(category);

        // when
        List<Category> result = categoryRepository.findByParentIsNull();

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("하위 카테고리와 같이 카테고리를 조회한다.")
    void findWithChildrenById() {
        // given
        Category parent = Category.createCategory("개발", null);
        categoryRepository.save(parent);
        Category child = Category.createCategory("백엔드", parent);
        categoryRepository.save(child);

        // when
        Category result = categoryRepository.findWithChildrenById(parent.getId()).get();

        // then
        assertThat(result).isEqualTo(parent);
        assertThat(result.getChildren().size()).isEqualTo(1);
    }

}