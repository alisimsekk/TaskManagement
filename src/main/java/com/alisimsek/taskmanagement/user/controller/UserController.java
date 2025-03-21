package com.alisimsek.taskmanagement.user.controller;

import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import com.alisimsek.taskmanagement.user.service.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Restricted to ADMIN role")
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
    }

    @Operation(summary = "Get user by GUID", description = "Restricted to ADMIN role")
    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByGuid(guid)));
    }

    @Operation(summary = "Get all users with pagination", description = "Restricted to ADMIN role")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(Pageable pageable) {
        Page<UserDto> usersPage = userService.getAllUsers(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(usersPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(usersPage.getContent()));
    }

    @Operation(summary = "Search users with criteria", description = "Restricted to ADMIN role")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDto>>> searchUsers(@ModelAttribute UserSearchRequest request, Pageable pageable) {
        Page<UserDto> usersPage = userService.searchUsers(request, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(usersPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(usersPage.getContent()));
    }

    @Operation(summary = "Update user details", description = "Restricted to ADMIN role")
    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable String guid, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(guid, request)));
    }

    @Operation(summary = "Delete a user", description = "Restricted to ADMIN role")
    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String guid) {
        userService.deleteUser(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @Operation(summary = "Activate a deleted user", description = "Restricted to ADMIN role")
    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<UserDto>> activateUser(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(userService.activateUser(guid)));
    }

    @Operation(summary = "Add role to a user", description = "Restricted to ADMIN role")
    @PostMapping("/{userGuid}/add-role/{userRoleGuid}")
    public ResponseEntity<ApiResponse<UserDto>> addUserRoleToUser(@PathVariable String userGuid, @PathVariable String userRoleGuid) {
        return ResponseEntity.ok(ApiResponse.success(userService.addUserRoleToUser(userGuid, userRoleGuid)));
    }
}
