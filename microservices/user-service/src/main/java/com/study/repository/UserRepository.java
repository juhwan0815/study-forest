package com.study.repository;

import com.study.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByKakaoId(Long kakaoId);
}
