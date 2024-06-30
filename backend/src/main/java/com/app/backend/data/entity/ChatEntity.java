package com.app.backend.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "chats")
public class ChatEntity extends BaseEntity {
    @Column(nullable = false)
    String message;

    @Column(nullable = false)
    String sender;
}
