package com.alisimsek.taskmanagement.task.controller.dto.request;

import com.alisimsek.taskmanagement.task.controller.dto.validation.ValidTaskStateAndReason;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidTaskStateAndReason
public class TaskStateUpdateRequest {
    @NotNull
    private TaskState state;
    private String cancellationReason;
    private String blockReason;
}
