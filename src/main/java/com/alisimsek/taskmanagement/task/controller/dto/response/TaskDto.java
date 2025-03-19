package com.alisimsek.taskmanagement.task.controller.dto.response;

import com.alisimsek.taskmanagement.attachment.controller.dto.response.AttachmentDto;
import com.alisimsek.taskmanagement.comment.controller.dto.response.CommentDto;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectDto;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskDto {
    private String guid;
    private String title;
    private String description;
    private String userStoryDescription;
    private String acceptanceCriteria;
    private TaskState state;
    private TaskPriority priority;
    private String cancellationReason;
    private String blockReason;
    private List<CommentDto> comments;
    private List<AttachmentDto> attachments;
    private UserDto assignee;
    private ProjectDto project;
    private EntityStatus entityStatus;
}
