package com.alisimsek.taskmanagement.role.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.role.controller.dto.request.UserRoleCreateRequest;
import com.alisimsek.taskmanagement.role.controller.dto.response.UserRoleDto;
import com.alisimsek.taskmanagement.role.service.UserRoleService;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(summary = "Create a new user role", description = "Restricted to ADMIN role")
    @PostMapping
    public ResponseEntity<ApiResponse<UserRoleDto>> createUserRole(@Valid @RequestBody UserRoleCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.createUserRole(request)));
    }

    @Operation(summary = "Get user role by GUID", description = "Restricted to ADMIN role")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<UserRoleDto>> getUserRoleByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.getUserRoleByGuid(guid)));
    }

    @Operation(summary = "Get all user roles with pagination", description = "Restricted to ADMIN role")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRoleDto>>> getAllUserRoles(Pageable pageable) {
        Page<UserRoleDto> userRolesPage = userRoleService.getAllUserRoles(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(userRolesPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(userRolesPage.getContent()));

    }

    @Operation(summary = "Search user roles with criteria", description = "Restricted to ADMIN role")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserRoleDto>>> searchUserRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "ACTIVE") EntityStatus entityStatus,
            Pageable pageable) {
        Page<UserRoleDto> userRolesPage = userRoleService.searchUserRoles(name, entityStatus, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(userRolesPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(userRolesPage.getContent()));
    }

    @Operation(summary = "Update user role details", description = "Restricted to ADMIN role")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<UserRoleDto>> updateUserRole(@PathVariable String guid, @Valid @RequestBody UserRoleCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.updateUserRole(guid, request)));
    }

    @Operation(summary = "Delete a user role", description = "Restricted to ADMIN role")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteUserRole(@PathVariable String guid) {
        userRoleService.deleteUserRole(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @Operation(summary = "Activate a deleted user role", description = "Restricted to ADMIN role")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<UserRoleDto>> activateUserRole(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.activateUserRole(guid)));
    }
}
