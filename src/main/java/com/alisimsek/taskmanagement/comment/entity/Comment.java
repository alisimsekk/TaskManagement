package com.alisimsek.taskmanagement.comment.entity;

import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Comment extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
}
