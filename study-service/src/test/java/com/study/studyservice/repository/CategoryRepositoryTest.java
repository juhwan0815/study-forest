package com.study.studyservice.repository;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("이름으로 삭제 상태가 아닌 카테고리를 조회한다.")
    void findByNameAndStatus(){
        // given
        Category category = Category.createCategory("토익", null);
        categoryRepository.save(category);

        em.flush();
        em.clear();

        // when
        Category result = categoryRepository
                .findByNameAndStatus("토익",CategoryStatus.ACTIVE).get();

        // then
        assertThat(result.getId()).isEqualTo(category.getId());
        assertThat(result.getName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("삭제 상태가 아닌 부모 카테고리를 조회한다.")
    void findByParentIsNullAndStatus(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        categoryRepository.save(parentCategory);
        categoryRepository.save(childCategory);

        em.flush();
        em.clear();

        // when
        List<Category> result = categoryRepository.findByParentIsNullAndStatus(CategoryStatus.ACTIVE);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(parentCategory.getId());
    }

    @Test
    @DisplayName("삭제 상태가 아닌 자식 카테고리를 조회한다")
    void findByParentAndStatus(){
        // given
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        categoryRepository.save(parentCategory);
        categoryRepository.save(childCategory);

        em.flush();
        em.clear();

        // when
        List<Category> result = categoryRepository
                .findByParentAndStatus(parentCategory,CategoryStatus.ACTIVE);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(childCategory.getId());
    }

    @Test
    @DisplayName("카테고리 ID 로 카테고리와 카테고리의 부모를 조회한다.")
    void findWithParentById(){
        Category parentCategory = Category.createCategory("개발", null);
        Category childCategory = Category.createCategory("백엔드",parentCategory);

        categoryRepository.save(parentCategory);
        categoryRepository.save(childCategory);

        em.flush();
        em.clear();

        Category result = categoryRepository.findWithParentById(childCategory.getId()).get();

        assertThat(result.getId()).isEqualTo(childCategory.getId());
        assertThat(result.getName()).isEqualTo(childCategory.getName());
        assertThat(result.getParent().getId()).isEqualTo(parentCategory.getId());
        assertThat(result.getParent().getName()).isEqualTo(parentCategory.getName());
    }
}