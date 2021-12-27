package com.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyUpdateRequest {

    private String name;

    private Integer numberOfPeople;

    private String content;

    private Boolean online;

    private Boolean offline;

    private Boolean open;

    private Long categoryId;
}
