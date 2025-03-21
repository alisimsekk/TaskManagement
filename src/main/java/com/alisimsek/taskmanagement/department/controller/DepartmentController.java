package com.alisimsek.taskmanagement.department.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.department.controller.dto.request.DepartmentCreateRequest;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "Create a new department", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.createDepartment(request)));
    }

    @Operation(summary = "Get department by GUID", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartmentByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getDepartmentByGuid(guid)));
    }

    @Operation(summary = "Get all departments with pagination", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments(Pageable pageable) {
        Page<DepartmentDto> departmentPage = departmentService.getAllDepartments(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(departmentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(departmentPage.getContent()));

    }

    @Operation(summary = "Search departments with criteria", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> searchDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "ACTIVE") EntityStatus entityStatus,
            Pageable pageable) {
        Page<DepartmentDto> departmentPage = departmentService.searchDepartments(name, entityStatus, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(departmentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(departmentPage.getContent()));
    }

    @Operation(summary = "Update department details", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(@PathVariable String guid, @Valid @RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.updateDepartment(guid, request)));
    }

    @Operation(summary = "Delete a department", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable String guid) {
        departmentService.deleteDepartment(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @Operation(summary = "Activate a deleted department", description = "Restricted to ADMIN and PROJECT_MANAGER")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<DepartmentDto>> activateDepartment(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.activateDepartment(guid)));
    }
}
