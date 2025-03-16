package com.alisimsek.taskmanagement.task.entity;

import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.comment.entity.Comment;
import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.CompletedTaskModificationException;
import com.alisimsek.taskmanagement.common.exception.InvalidTaskStateTransitionException;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(columnDefinition = "TEXT")
    private String blockReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.LOW;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public void addAttachment(Attachment attachment) {
        Optional<Attachment> attachmentOptional = this.attachments.stream().filter(atc -> atc.getUniqueFilename().equals(attachment.getUniqueFilename())).findFirst();
        if (attachmentOptional.isPresent()){
            attachmentOptional.get().activate();
            return;
        }
        attachment.setTask(this);
        this.attachments.add(attachment);
    }

    public void updateAttachments(List<Attachment> attachmentList) {
        this.attachments.forEach(Attachment::delete);
        attachmentList.forEach(this::addAttachment);
    }

    public List<Attachment> getAttachments() {
        return this.attachments.stream().filter(attachment -> EntityStatus.ACTIVE.equals(attachment.getEntityStatus())).collect(Collectors.toList());
    }

    public void deleteAllAttachments() {
        this.attachments.forEach(Attachment::delete);
    }

    public void removeAttachment(Attachment attachment) {
        attachment.delete();
        this.attachments.remove(attachment);
    }

    public List<Comment> getComments() {
        return this.comments.stream().filter(comment -> EntityStatus.ACTIVE.equals(comment.getEntityStatus())).collect(Collectors.toList());
    }

    public void updateState(TaskState newState, String cancellationReason, String blockReason) {
        if (this.state == TaskState.COMPLETED) {
            throw new CompletedTaskModificationException();
        }

        if (!isValidHappyPathTransition(this.state, newState)) {
            throw new InvalidTaskStateTransitionException();
        }

        this.state = newState;
        clearPreviousReasons();
        applyStateSpecificRules(newState, cancellationReason, blockReason);
    }

    private boolean isValidHappyPathTransition(TaskState currentState, TaskState nextState) {
        if (currentState == nextState) {
            return true;
        }

        return switch (currentState) {
            case BACKLOG -> nextState == TaskState.IN_ANALYSIS || nextState == TaskState.CANCELLED;
            case IN_ANALYSIS -> nextState == TaskState.BACKLOG || nextState == TaskState.IN_DEVELOPMENT || nextState == TaskState.BLOCKED || nextState == TaskState.CANCELLED;
            case IN_DEVELOPMENT -> nextState == TaskState.IN_ANALYSIS || nextState == TaskState.COMPLETED || nextState == TaskState.BLOCKED || nextState == TaskState.CANCELLED;
            case BLOCKED -> nextState == TaskState.IN_ANALYSIS || nextState == TaskState.IN_DEVELOPMENT || nextState == TaskState.CANCELLED;
            default -> false;
        };
    }

    private void clearPreviousReasons() {
        if (!TaskState.BLOCKED.equals(this.state)) {
            this.blockReason = null;
        }
        if (!TaskState.CANCELLED.equals(this.state)) {
            this.cancellationReason = null;
        }
    }

    private void applyStateSpecificRules(TaskState newState, String cancellationReason, String blockReason) {
        if (TaskState.BLOCKED.equals(newState)) {
            this.blockReason = blockReason;
        } else if (TaskState.CANCELLED.equals(newState)) {
            this.cancellationReason = cancellationReason;
        }
    }
}
