package com.app.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.data.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByEmail(String email);
}
