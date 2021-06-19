package com.study.studyservice.model.category.response;

import com.study.studyservice.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;


    public static CategoryResponse from(Category category){
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.id = category.getId();
        categoryResponse.name = category.getName();
        return categoryResponse;
    }
}
