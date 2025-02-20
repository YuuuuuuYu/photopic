package com.swyp8team2.common.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean deleted = false;
    private LocalDateTime deletedAt;
}
