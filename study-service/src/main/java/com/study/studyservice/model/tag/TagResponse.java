package com.study.studyservice.model.tag;

import com.study.studyservice.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {

    private Long id;

    private String name;

    public static TagResponse from(Tag tag){
        TagResponse tagResponse = new TagResponse();
        tagResponse.id = tag.getId();
        tagResponse.name = tag.getName();
        return tagResponse;
    }
}
