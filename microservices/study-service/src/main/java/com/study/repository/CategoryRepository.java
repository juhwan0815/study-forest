package com.study.repository;

import com.study.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface
CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByParentIsNull();

    @Query("select distinct c from Category c left join fetch c.children where c.id =:categoryId")
    Optional<Category> findWithChildrenById(@Param("categoryId") Long categoryId);

    @Query("select c from Category c left join fetch c.parent where c.id =:categoryId")
    Optional<Category> findWithParentById(@Param("categoryId") Long categoryId);
}
