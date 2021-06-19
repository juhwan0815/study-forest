package com.study.studyservice.fixture;

import com.study.studyservice.domain.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagFixture {

    public static final Tag TEST_TAG1 = new Tag(1L,"JPA");

    public static final Tag TEST_TAG2 = new Tag(2L,"스프링");

    public static final Tag TEST_TAG3 = new Tag(3L,"노드");

    public static final Tag TEST_TAG4 = new Tag(4L,"자바");

    public static final List<Tag> TEST_TAG_LIST = Arrays.asList(TEST_TAG1,TEST_TAG2);

    public static List<Tag> createTestTagList(){
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(1L,"JPA"));
        tags.add(new Tag(2L,"JPA"));
        return tags;
    }




}
