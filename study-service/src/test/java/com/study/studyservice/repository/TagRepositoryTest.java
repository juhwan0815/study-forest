package com.study.studyservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.studyservice.domain.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("태그 in 조회")
    void findByNameIn(){
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("노드");
        Tag tag3 = Tag.createTag("타입스크립트");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);

        List<String> tagNames = Arrays.asList("스프링", "노드", "타입스크립트");
        List<Tag> tags = tagRepository.findByNameIn(tagNames);

        em.flush();
        em.clear();

        assertThat(tags.size()).isEqualTo(3);
        assertThat(tags).contains(tag1);
        assertThat(tags).contains(tag2);
        assertThat(tags).contains(tag3);
    }

    @Test
    @DisplayName("검색어가 포함된 태그를 페이징 조회한다.")
    void findByNameContainPaging() throws JsonProcessingException {
        Tag tag1 = Tag.createTag("스프링");
        Tag tag2 = Tag.createTag("스프링1");
        Tag tag3 = Tag.createTag("스프링2");
        Tag tag4 = Tag.createTag("스프링3");
        Tag tag5 = Tag.createTag("스프링4");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        tagRepository.save(tag4);
        tagRepository.save(tag5);

        em.flush();
        em.clear();

        PageRequest pageable = PageRequest.of(0, 3);
        Page<Tag> result = tagRepository.findByNameContaining(pageable, "프링");

        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

}