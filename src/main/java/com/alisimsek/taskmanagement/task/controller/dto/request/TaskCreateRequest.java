package com.alisimsek.taskmanagement.task.controller.dto.request;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TaskCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private String userStoryDescription;
    private String acceptanceCriteria;
    private TaskState state;
    private TaskPriority priority;
    private List<AttachmentCreateDto> attachments;
    @NotBlank
    private String projectGuid;
}
