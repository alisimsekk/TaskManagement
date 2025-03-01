package com.alisimsek.taskmanagement.common.base;

import com.alisimsek.taskmanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String guid = UUID.randomUUID().toString();

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User modifiedBy;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus = EntityStatus.ACTIVE;

    public void delete() {
        if (EntityStatus.ACTIVE.equals(getEntityStatus())) {
            this.entityStatus = EntityStatus.DELETED;
        }
    }

    public void activate() {
        if (EntityStatus.DELETED.equals(getEntityStatus())) {
            this.entityStatus = EntityStatus.ACTIVE;
        }
    }
}
