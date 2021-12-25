package com.study.dto.tag;

import com.study.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {

    private Long tagId;

    private String content;

    public static TagResponse from(Tag tag){
        return new TagResponse(tag.getId(), tag.getContent());1
    }
}
