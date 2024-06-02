package com.app.backend.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "cvs")
public class CVEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Lob

    @Column(nullable = false)
    private byte[] fileData;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String userEmail;

}
