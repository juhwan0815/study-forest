package com.study.repository;

import com.study.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void init(){
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("이름으로 카테고리를 조회한다.")
    void findByName() {
        // given
        Category category = Category.createCategory("백엔드", null);
        categoryRepository.save(category);

        em.flush();
        em.clear();

        // when
        Category result = categoryRepository.findByName("백엔드").get();

        // then
        assertThat(result.getName()).isEqualTo("백엔드");
    }

    @Test
    @DisplayName("상위 카테고리를 조회한다.")
    void findByParentIsNull() {
        // given
        Category category = Category.createCategory("백엔드", null);
        categoryRepository.save(category);

        em.flush();
        em.clear();

        // when
        List<Category> result = categoryRepository.findByParentIsNull();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("하위 카테고리와 같이 카테고리를 조회한다.")
    void findWithChildrenById() {
        // given
        Category parent = Category.createCategory("개발", null);
        categoryRepository.save(parent);
        Category child = Category.createCategory("백엔드", parent );
        categoryRepository.save(child);

        em.flush();
        em.clear();

        // when
        Category result = categoryRepository.findWithChildrenById(parent.getId()).get();

        // then
        assertThat(result.getId()).isEqualTo(parent.getId());
        assertThat(result.getChildren().size()).isEqualTo(1);
        assertThat(result.getChildren().get(0).getId()).isEqualTo(child.getId());
    }

    @Test
    @DisplayName("상위 카테고리와 같이 카테고리를 조회한다.")
    void findWithParentById() {
        // given
        Category parent = Category.createCategory("개발", null);
        categoryRepository.save(parent);
        Category child = Category.createCategory("백엔드", parent );
        categoryRepository.save(child);

        em.flush();
        em.clear();

        // when
        Category result = categoryRepository.findWithParentById(child.getId()).get();

        // then
        assertThat(result.getId()).isEqualTo(child.getId());
        assertThat(result.getParent().getId()).isEqualTo(parent.getId());
    }
}