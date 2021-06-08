package com.study.studyservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    @DisplayName("카테고리 이름 변경")
    void changeName(){
        Category category = Category.createCategory("개발", null);

        category.changeName("영어");

        assertThat(category.getName()).isEqualTo("영어");
    }

    @Test
    @DisplayName("카테고리 삭제")
    void delete(){
        Category category = Category.createCategory("개발", null);

        category.delete();;

        assertThat(category.getStatus()).isEqualTo(CategoryStatus.DELETE);
    }
}