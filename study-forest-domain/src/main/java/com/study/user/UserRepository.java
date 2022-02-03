package com.study.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(Long kakaoId);

    @Query("select u from User u left join fetch u.area where u.id=:usreId")
    Optional<User> findWithAreaById(@Param("userId") Long userId);
}
