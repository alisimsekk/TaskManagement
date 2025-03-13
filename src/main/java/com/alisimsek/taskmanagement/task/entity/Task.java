package com.alisimsek.taskmanagement.task.entity;

import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.comment.entity.Comment;
import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String userStoryDescription;

    @Column(columnDefinition = "TEXT")
    private String acceptanceCriteria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskState state = TaskState.BACKLOG;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.LOW;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    @OneToMany(mappedBy = "task")
    private List<Attachment> attachments;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
