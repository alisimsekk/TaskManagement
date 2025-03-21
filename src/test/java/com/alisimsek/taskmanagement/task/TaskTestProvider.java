package com.alisimsek.taskmanagement.task;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.task.controller.dto.request.*;
import com.alisimsek.taskmanagement.task.controller.dto.response.TaskDto;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.entity.UserType;

import java.util.Collections;

public class TaskTestProvider {

    public static String PROJECT_GUID = "project-guid";
    public static String PROJECT_TITLE = "Test Project";
    public static String TASK_GUID = "task-guid";
    public static String TASK_TITLE = "Test Task";
    public static String TASK_BASE_URI = "/api/v1/tasks";
    public static String ATTACHMENT_GUID = "attachment-123";
    public static String TEAM_MEMBER_GUID = "user-123";

    public static Task getTask() {
        Task task = new Task();
        task.setGuid(TASK_GUID);
        task.setTitle(TASK_TITLE);
        task.setDescription("Test Description");
        task.setUserStoryDescription("User Story");
        task.setAcceptanceCriteria("Acceptance Criteria");
        task.setState(TaskState.IN_ANALYSIS);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProject(getProject());
        return task;
    }

    public static Project getProject() {
        Project project = new Project();
        project.setGuid(PROJECT_GUID);
        project.setTitle(PROJECT_TITLE);
        return project;
    }

    public static TaskDto getTaskDto() {
        return TaskDto.builder()
                .guid(TASK_GUID)
                .title(TASK_TITLE)
                .description("Test Description")
                .state(TaskState.IN_ANALYSIS)
                .priority(TaskPriority.MEDIUM)
                .build();
    }

    public static TaskCreateRequest getTaskCreateRequest() {
        TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
        taskCreateRequest.setTitle("New Task");
        taskCreateRequest.setDescription("New Description");
        taskCreateRequest.setProjectGuid(PROJECT_GUID);
        taskCreateRequest.setAttachments(Collections.singletonList(getAttachmentCreateDto()));
        return taskCreateRequest;
    }

    public static AttachmentCreateDto getAttachmentCreateDto() {
        AttachmentCreateDto dto = new AttachmentCreateDto();
        dto.setOriginalFilename("test-file.pdf");
        dto.setUniqueFilename("uniquetest-file.pdf");
        dto.setFileDownloadUri("/files/uniquetest-file.pdf");
        return dto;
    }

    public static TaskUpdateRequest getTaskUpdateRequest() {
        TaskUpdateRequest taskUpdateRequest = new TaskUpdateRequest();
        taskUpdateRequest.setTitle("Updated Task");
        taskUpdateRequest.setDescription("Updated Description");
        taskUpdateRequest.setUserStoryDescription("Updated User Story");
        taskUpdateRequest.setAcceptanceCriteria("Updated Criteria");
        taskUpdateRequest.setPriority(TaskPriority.HIGH);
        taskUpdateRequest.setAttachments(Collections.singletonList(getAttachmentCreateDto()));
        return taskUpdateRequest;
    }

    public static TaskStateUpdateRequest getTaskStateUpdateRequest() {
        TaskStateUpdateRequest taskStateUpdateRequest = new TaskStateUpdateRequest();
        taskStateUpdateRequest.setState(TaskState.IN_DEVELOPMENT);
        taskStateUpdateRequest.setBlockReason(null);
        taskStateUpdateRequest.setCancellationReason(null);
        return taskStateUpdateRequest;
    }

    public static AddAttachmentRequest getAddAttachmentRequest() {
        AddAttachmentRequest addAttachmentRequest = new AddAttachmentRequest();
        addAttachmentRequest.setAttachments(Collections.singletonList(getAttachmentCreateDto()));
        return addAttachmentRequest;
    }

    public static Attachment getAttachment() {
        Attachment attachment = new Attachment();
        attachment.setGuid(ATTACHMENT_GUID);
        attachment.setOriginalFilename("test-file.pdf");
        attachment.setUniqueFilename("uniquetest-file.pdf");
        attachment.setFileDownloadUri("/files/uniquetest-file.pdf");
        attachment.setTask(getTask());
        attachment.setEntityStatus(EntityStatus.ACTIVE);
        return attachment;
    }

    public static User getUser() {
        User teamMember = new User();
        teamMember.setGuid(TEAM_MEMBER_GUID);
        teamMember.setUserType(UserType.TEAM_MEMBER);
        return teamMember;
    }

    public static User getNonTeamMemberUser() {
        User nonTeamMember = new User();
        nonTeamMember.setGuid("non-team-member-guid");
        nonTeamMember.setUserType(UserType.PROJECT_MANAGER);
        return nonTeamMember;
    }

    public static TaskSearchRequest getTaskSearchRequest() {
        TaskSearchRequest taskSearchRequest = new TaskSearchRequest();
        taskSearchRequest.setTitle(TASK_TITLE);
        taskSearchRequest.setState(TaskState.IN_ANALYSIS);
        taskSearchRequest.setPriority(TaskPriority.MEDIUM);
        taskSearchRequest.setProjectGuid(PROJECT_GUID);
        return taskSearchRequest;
    }
}
