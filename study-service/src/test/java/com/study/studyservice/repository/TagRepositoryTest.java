package com.study.studyservice.repository;

import com.study.studyservice.domain.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("태그 저장")
    void save() {
        Tag tag = Tag.createTag("스프링");

        Tag savedTag = tagRepository.save(tag);

        assertThat(savedTag.getId()).isNotNull();
    }
    
    @Test
    @DisplayName("태그 in 조회")
    void findByNameIn(){
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("노드");
        Tag tag3 = Tag.createTag("타입스크립트");
        Tag tag4 = Tag.createTag("자바스크립트");
        Tag tag5 = Tag.createTag("데이터베이스");

        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        tagRepository.save(tag4);
        tagRepository.save(tag5);

        List<String> tagNames = Arrays.asList("스프링", "노드", "타입스크립트", "자바스크립트", "데이터베이스");
        List<Tag> tags = tagRepository.findByNameIn(tagNames);

        assertThat(tags.size()).isEqualTo(5);
        assertThat(tags).contains(tag1);
        assertThat(tags).contains(tag2);
        assertThat(tags).contains(tag3);
        assertThat(tags).contains(tag4);
        assertThat(tags).contains(tag5);
    }

}