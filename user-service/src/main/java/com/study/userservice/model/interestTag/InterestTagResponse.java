package com.study.userservice.model.interestTag;

import com.study.userservice.domain.InterestTag;
import com.study.userservice.model.tag.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestTagResponse {

    private Long id;

    private Long tagId;

    private String name;

    public static InterestTagResponse from(InterestTag interestTag, TagResponse tagResponse){
        InterestTagResponse interestTagResponse = new InterestTagResponse();
        interestTagResponse.id = interestTag.getId();
        interestTagResponse.tagId = interestTag.getTagId();
        interestTagResponse.name = tagResponse.getName();
        return interestTagResponse;
    }
}
