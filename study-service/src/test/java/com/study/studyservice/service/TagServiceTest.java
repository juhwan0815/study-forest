package com.study.studyservice.service;

import com.study.studyservice.domain.Tag;
import com.study.studyservice.fixture.TagFixture;
import com.study.studyservice.repository.TagRepository;
import com.study.studyservice.service.impl.TagServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.studyservice.fixture.TagFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagRepository tagRepository;

    @Test
    @DisplayName("태그 이름으로 태그를 조회하고 없으면 태그를 저장한다.")
    void findAndCreate(){

        List<String> requestTags = Arrays.asList("JPA","스프링","노드");

        List<Tag> tags =  new ArrayList<>();
        tags.add(TEST_TAG1);
        tags.add(TEST_TAG2);

        given(tagRepository.findByNameIn(any()))
                .willReturn(tags);

        given(tagRepository.save(any()))
                .willReturn(TEST_TAG3);

        List<Tag> result = tagService.FindAndCreate(requestTags);

        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(2)).isEqualTo(TEST_TAG3);
        then(tagRepository).should(times(1)).findByNameIn(any());
        then(tagRepository).should(times(1)).save(any());
    }



}