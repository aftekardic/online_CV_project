package com.app.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.data.entity.CVEntity;

public interface CVRepository extends JpaRepository<CVEntity, Long> {
    public CVEntity findByUserEmail(String userEmail);
}
