package com.alisimsek.taskmanagement.task.controller;

import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.task.controller.dto.request.*;
import com.alisimsek.taskmanagement.task.controller.dto.response.TaskDto;
import com.alisimsek.taskmanagement.task.service.TaskService;
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

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.createTask(request)));
    }

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<TaskDto>> getTaskByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskByGuid(guid)));
    }

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDto>>> getAllTasks(Pageable pageable) {
        Page<TaskDto> taskPage = taskService.getAllTasks(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(taskPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(taskPage.getContent()));
    }

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TaskDto>>> searchTask(@ModelAttribute TaskSearchRequest request, Pageable pageable) {
        Page<TaskDto> taskPage = taskService.searchTask(request, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(taskPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(taskPage.getContent()));
    }

    @PreAuthorize("hasAuthority('SET_TASK_PRIORITY')")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(@PathVariable String guid, @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(guid, request)));
    }

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String guid) {
        taskService.deleteTask(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<TaskDto>> activateTask(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.activateTask(guid)));
    }

    @PreAuthorize("hasAuthority('UPDATE_TASK_STATE')")
    @PutMapping("/{guid}/update-state")
    public ResponseEntity<ApiResponse<TaskDto>> updateTaskState(@PathVariable String guid, @Valid @RequestBody TaskStateUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTaskState(guid, request)));
    }

    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @PostMapping("/{guid}/add-attachments")
    public ResponseEntity<ApiResponse<TaskDto>> addAttachmentsToTask(@PathVariable String guid, @Valid @RequestBody AddAttachmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.addAttachmentsToTask(guid, request)));
    }

    @PreAuthorize("hasAuthority('MANAGE_TASKS')")
    @PostMapping("/{taskGuid}/remove-attachment/{attachmentGuid}")
    public ResponseEntity<ApiResponse<TaskDto>> removeAttachmentFromTask(@PathVariable String taskGuid, @PathVariable String attachmentGuid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.removeAttachmentFromTask(taskGuid, attachmentGuid)));
    }

    @PreAuthorize("hasAuthority('ASSIGN_TEAM_MEMBER')")
    @PostMapping("/{taskGuid}/assign/{teamMemberGuid}")
    public ResponseEntity<ApiResponse<TaskDto>> assignTaskToTeamMember(@PathVariable String taskGuid, @PathVariable String teamMemberGuid) {
        return ResponseEntity.ok(ApiResponse.success(taskService.assignTaskToTeamMember(taskGuid, teamMemberGuid)));
    }
}
