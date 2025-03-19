package com.alisimsek.taskmanagement.project.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddDepartmentToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddTeamMemberToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.ProjectCreateRequest;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectDto;
import com.alisimsek.taskmanagement.project.entity.ProjectStatus;
import com.alisimsek.taskmanagement.project.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(projectService.createProject(request)));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<ProjectDto>> getProjectByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjectByGuid(guid)));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getAllProjects(Pageable pageable) {
        Page<ProjectDto> projectsPage = projectService.getAllProjects(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(projectsPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(projectsPage.getContent()));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> searchProjects(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false, defaultValue = "ACTIVE") EntityStatus entityStatus,
            Pageable pageable) {
        Page<ProjectDto> projectPage = projectService.searchProjects(title, status, departmentName, entityStatus, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(projectPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(projectPage.getContent()));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject(@PathVariable String guid, @Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(projectService.updateProject(guid, request)));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String guid) {
        projectService.deleteProject(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<ProjectDto>> activateProject(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(projectService.activateProject(guid)));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @PostMapping("/add-department")
    public ResponseEntity<ApiResponse<ProjectDto>> addDepartmentToProject(@Valid @RequestBody AddDepartmentToProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(projectService.addDepartmentToProject(request)));
    }

    @PreAuthorize("hasAuthority('MANAGE_PROJECTS')")
    @PostMapping("/add-team-member")
    public ResponseEntity<ApiResponse<ProjectDto>> addTeamMemberToProject(@Valid @RequestBody AddTeamMemberToProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(projectService.addTeamMemberToProject(request)));
    }
}
