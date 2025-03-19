package com.alisimsek.taskmanagement.task.controller.dto.request;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TaskUpdateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String userStoryDescription;
    @NotBlank
    private String acceptanceCriteria;
    @NotNull
    private TaskPriority priority;
    private List<AttachmentCreateDto> attachments;
}
