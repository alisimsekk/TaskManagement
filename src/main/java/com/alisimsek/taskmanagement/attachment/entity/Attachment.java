package com.alisimsek.taskmanagement.attachment.entity;

import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Attachment extends BaseEntity {

    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
