package com.study.studyservice.repository;

import com.study.studyservice.domain.Category;
import com.study.studyservice.domain.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByNameAndStatus(String name,CategoryStatus status);

    List<Category> findByParentIsNullAndStatus(CategoryStatus status);

    List<Category> findByParentAndStatus(Category category,CategoryStatus status);

    @Query("select distinct c from Category c left join fetch c.parent where c.id =:categoryId")
    Optional<Category> findWithParentById(@Param("categoryId") Long categoryId);
}
