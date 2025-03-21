package com.alisimsek.taskmanagement.task.controller;

import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.task.controller.dto.request.*;
import com.alisimsek.taskmanagement.task.controller.dto.response.TaskDto;
import com.alisimsek.taskmanagement.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Permission: MANAGE_TASKS")
    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.createTask(request)));
    }

    @Operation(summary = "Get task by GUID", description = "Available for authenticated users")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<TaskDto>> getTaskByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskByGuid(guid)));
    }

    @Operation(summary = "Get all tasks with pagination", description = "Available for authenticated users")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDto>>> getAllTasks(Pageable pageable) {
        Page<TaskDto> taskPage = taskService.getAllTasks(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(taskPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(taskPage.getContent()));
    }

    @Operation(summary = "Search tasks with criteria", description = "Available for authenticated users")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TaskDto>>> searchTask(@ModelAttribute TaskSearchRequest request, Pageable pageable) {
        Page<TaskDto> taskPage = taskService.searchTask(request, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(taskPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(taskPage.getContent()));
    }

    @Operation(summary = "Update task details", description = "Permission: SET_TASK_PRIORITY")
    @PreAuthorize("hasAuthority('SET_TASK_PRIORITY')")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(@PathVariable String guid, @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(guid, request)));
    }

    @Operation(summary = "Delete a task", description = "Permission: MANAGE_TASKS")
    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String guid) {
        taskService.deleteTask(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @Operation(summary = "Activate a deleted task", description = "Permission: MANAGE_TASKS")
    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<TaskDto>> activateTask(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.activateTask(guid)));
    }

    @Operation(summary = "Update task state", description = "Permission: UPDATE_TASK_STATE")
    @PreAuthorize("hasAuthority('UPDATE_TASK_STATE')")
    @PutMapping("/{guid}/update-state")
    public ResponseEntity<ApiResponse<TaskDto>> updateTaskState(@PathVariable String guid, @Valid @RequestBody TaskStateUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTaskState(guid, request)));
    }

    @Operation(summary = "Add attachments to a task", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping("/{guid}/add-attachments")
    public ResponseEntity<ApiResponse<TaskDto>> addAttachmentsToTask(@PathVariable String guid, @Valid @RequestBody AddAttachmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.addAttachmentsToTask(guid, request)));
    }

    @Operation(summary = "Remove attachment from a task", description = "Permission: ADD_ATTACHMENT")
    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping("/{taskGuid}/remove-attachment/{attachmentGuid}")
    public ResponseEntity<ApiResponse<TaskDto>> removeAttachmentFromTask(@PathVariable String taskGuid, @PathVariable String attachmentGuid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.removeAttachmentFromTask(taskGuid, attachmentGuid)));
    }

    @Operation(summary = "Assign task to a team member", description = "Permission: ASSIGN_TEAM_MEMBER")
    @PreAuthorize("hasAuthority('ASSIGN_TEAM_MEMBER')")
    @PostMapping("/{taskGuid}/assign/{teamMemberGuid}")
    public ResponseEntity<ApiResponse<TaskDto>> assignTaskToTeamMember(@PathVariable String taskGuid, @PathVariable String teamMemberGuid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.assignTaskToTeamMember(taskGuid, teamMemberGuid)));
    }
}
