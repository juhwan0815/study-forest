package com.study.repository;

import com.study.domain.Auth;
import org.springframework.data.repository.CrudRepository;

public interface AuthRepository extends CrudRepository<Auth, Long> {
}
