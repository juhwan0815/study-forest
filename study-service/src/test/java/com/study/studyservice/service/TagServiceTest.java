package com.study.studyservice.service;

import com.study.studyservice.domain.Tag;
import com.study.studyservice.fixture.TagFixture;
import com.study.studyservice.model.tag.TagResponse;
import com.study.studyservice.repository.TagRepository;
import com.study.studyservice.service.impl.TagServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.study.studyservice.fixture.TagFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    @DisplayName("태그 이름으로 태그를 검색한다.")
    void findListName(){
        // given
        PageRequest pageable = PageRequest.of(0, 2);
        Page<Tag> pageTags = new PageImpl<>(TEST_TAG_LIST2, pageable, TEST_TAG_LIST2.size());

        given(tagRepository.findByNameContaining(any(),any()))
                .willReturn(pageTags);

        // when
        Page<TagResponse> result = tagService.findLikeName(pageable, TEST_TAG_SEARCH_REQUEST);

        // then
        assertThat(result.getTotalElements()).isEqualTo(TEST_TAG_LIST2.size());
        assertThat(result.getTotalPages()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getName()).isEqualTo(TEST_TAG5.getName());
        assertThat(result.getContent().get(1).getName()).isEqualTo(TEST_TAG6.getName());
    }

    @Test
    @DisplayName("태그 ID 리스트로 태그를 조회한다.")
    void findByIdIn(){
        List<Long> tagIdList = Arrays.asList(1L, 2L);

        given(tagRepository.findByIdIn(tagIdList))
                .willReturn(TEST_TAG_LIST);

        List<TagResponse> result = tagService.findByIdIn(TEST_TAG_FIND_REQUEST);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);

        then(tagRepository).should(times(1)).findByIdIn(any());
    }

}