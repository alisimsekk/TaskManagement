package com.alisimsek.taskmanagement.task.controller.dto.request;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import lombok.Data;

@Data
public class TaskSearchRequest {
    private String title;
    private TaskState state;
    private TaskPriority priority;
    private String assigneeUserGuid;
    private String projectGuid;
    private String projectTitle;
    private EntityStatus entityStatus = EntityStatus.ACTIVE;
}
