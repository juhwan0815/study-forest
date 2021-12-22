package com.study.dto.keyword;

import com.study.domain.Keyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordResponse {

    private Long keywordId;

    private String content;

    public static KeywordResponse from(Keyword keyword){
        return new KeywordResponse(keyword.getId(), keyword.getContent());
    }
}
