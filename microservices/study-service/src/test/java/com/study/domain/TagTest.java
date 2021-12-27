package com.study.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TagTest {

    @Test
    @DisplayName("스터디 태그를 생성한다.")
    void createTag() {
        // given
        String content = "스프링";

        // when
        Tag result = Tag.createTag(content, null);

        // then
        assertThat(result.getContent()).isEqualTo(content);
    }
}