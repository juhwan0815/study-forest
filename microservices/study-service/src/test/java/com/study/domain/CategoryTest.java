package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    @DisplayName("카테고리를 생성한다.")
    void createCategory() {
        // given
        String name = "개발";

        // when
        Category result = Category.createCategory(name, null);

        // then
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getParent()).isNull();
    }

    @Test
    @DisplayName("자식 카테고리를 생성한다.")
    void createChildrenCategory() {
        // given
        Category parent = Category.createCategory("개발", null);

        // when
        Category result = Category.createCategory("백엔드", parent);

        // then
        assertThat(result.getName()).isEqualTo("백엔드");
        assertThat(result.getParent()).isEqualTo(parent);
    }

    @Test
    @DisplayName("카테고리의 이름을 변경한다.")
    void changeName() {
        // given
        Category category = Category.createCategory("백엔드", null);

        // when
        category.changeName("프론트엔드");

        // then
        assertThat(category.getName()).isEqualTo("프론트엔드");
    }
}