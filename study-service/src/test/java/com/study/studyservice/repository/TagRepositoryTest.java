package com.study.studyservice.repository;

import com.study.studyservice.domain.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

}