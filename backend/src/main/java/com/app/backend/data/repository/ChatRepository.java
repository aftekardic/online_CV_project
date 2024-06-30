package com.app.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.data.entity.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

}
