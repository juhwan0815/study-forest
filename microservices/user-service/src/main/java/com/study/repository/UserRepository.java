package com.study.repository;

import com.study.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(Long kakaoId);

    List<User> findByIdIn(List<Long> userIds);

    @Query("select u from User u left join fetch u.keywords where u.id =:userId")
    Optional<User> findWithKeywordById(@Param("userId") Long userId);

}
