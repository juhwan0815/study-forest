package com.study.studyservice.fixture;

import com.study.studyservice.domain.Tag;
import com.study.studyservice.model.tag.TagFindRequest;
import com.study.studyservice.model.tag.TagResponse;
import com.study.studyservice.model.tag.TagSearchRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagFixture {

    public static final TagSearchRequest TEST_TAG_SEARCH_REQUEST = new TagSearchRequest("프링");

    public static final TagFindRequest TEST_TAG_FIND_REQUEST = new TagFindRequest(Arrays.asList(1L,2L));

    public static final Tag TEST_TAG1 = new Tag(1L,"JPA");

    public static final Tag TEST_TAG2 = new Tag(2L,"스프링");

    public static final Tag TEST_TAG3 = new Tag(3L,"노드");

    public static final Tag TEST_TAG4 = new Tag(4L,"자바");

    public static final Tag TEST_TAG5 = new Tag(5L,"스프링1");

    public static final Tag TEST_TAG6 = new Tag(6L,"스프링2");

    public static final TagResponse TEST_TAG_RESPONSE1 = new TagResponse(1L,"스프링");
    public static final TagResponse TEST_TAG_RESPONSE2 = new TagResponse(2L,"스프링 DATA JPA");

    public static final List<Tag> TEST_TAG_LIST = Arrays.asList(TEST_TAG1,TEST_TAG2);

    public static final List<Tag> TEST_TAG_LIST2 = Arrays.asList(TEST_TAG5,TEST_TAG6);

    public static List<Tag> createTestTagList(){
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(1L,"JPA"));
        tags.add(new Tag(2L,"JPA"));
        return tags;
    }




}
