package com.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateRequest {

    private String name;

    private Integer numberOfPeople;

    private String content;

    private List<String> tags;

    private Boolean online;

    private Boolean offline;

    private String areaCode;

    private Long categoryId;
}
