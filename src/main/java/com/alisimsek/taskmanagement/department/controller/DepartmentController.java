package com.alisimsek.taskmanagement.department.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.department.controller.dto.request.DepartmentCreateRequest;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.service.DepartmentService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.createDepartment(request)));
    }

    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartmentByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getDepartmentByGuid(guid)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments(Pageable pageable) {
        Page<DepartmentDto> departmentPage = departmentService.getAllDepartments(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(departmentPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(departmentPage.getContent()));

    }

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

    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(@PathVariable String guid, @Valid @RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.updateDepartment(guid, request)));
    }

    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable String guid) {
        departmentService.deleteDepartment(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<DepartmentDto>> activateDepartment(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.activateDepartment(guid)));
    }
}
