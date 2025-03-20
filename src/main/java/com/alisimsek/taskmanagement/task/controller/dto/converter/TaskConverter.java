package com.alisimsek.taskmanagement.task.controller.dto.converter;

import com.alisimsek.taskmanagement.attachment.converter.AttachmentConverter;
import com.alisimsek.taskmanagement.comment.converter.CommentConverter;
import com.alisimsek.taskmanagement.common.BaseConverter;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.controller.dto.converter.ProjectConverter;
import com.alisimsek.taskmanagement.task.controller.dto.request.TaskCreateRequest;
import com.alisimsek.taskmanagement.task.controller.dto.response.TaskDto;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import com.alisimsek.taskmanagement.user.controller.dto.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskConverter extends BaseConverter<Task, TaskDto> {

    private final CommentConverter commentConverter;
    private final ProjectConverter projectConverter;
    private final AttachmentConverter attachmentConverter;
    private final UserConverter userConverter;

    @Override
    public TaskDto convert(Task source) {
        return TaskDto.builder()
                .guid(source.getGuid())
                .entityStatus(source.getEntityStatus())
                .title(source.getTitle())
                .description(source.getDescription())
                .userStoryDescription(source.getUserStoryDescription())
                .acceptanceCriteria(source.getAcceptanceCriteria())
                .state(source.getState())
                .cancellationReason(source.getCancellationReason())
                .blockReason(source.getBlockReason())
                .priority(source.getPriority())
                .comments(Objects.nonNull(source.getComments()) ? commentConverter.convertList(source.getComments()) : null)
                .attachments(Objects.nonNull(source.getAttachments()) ? source.getAttachments().stream()
                        .filter(attachment -> EntityStatus.ACTIVE.equals(attachment.getEntityStatus()))
                        .map(attachmentConverter::convert).collect(Collectors.toList()) : null)
                .assignee(Objects.nonNull(source.getAssignee()) ? userConverter.convert(source.getAssignee()) : null)
                .project(Objects.nonNull(source.getProject()) ? projectConverter.convert(source.getProject()) : null)
                .build();
    }

    public Task convertToEntity(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserStoryDescription(request.getUserStoryDescription());
        task.setAcceptanceCriteria(request.getAcceptanceCriteria());
        task.setState(Optional.ofNullable(request.getState()).orElse(TaskState.BACKLOG));
        task.setPriority(Optional.ofNullable(request.getPriority()).orElse(TaskPriority.LOW));
        return task;
    }
}
