package com.alisimsek.taskmanagement.attachment.entity;

import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Attachment extends BaseEntity {
    @Column(nullable = false)
    private String uniqueFilename;
    @Column(nullable = false)
    private String originalFilename;
    @Column(nullable = false)
    private String fileDownloadUri;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
