package com.alisimsek.taskmanagement.user.controller;

import com.alisimsek.taskmanagement.common.response.ApiResponse;
import com.alisimsek.taskmanagement.common.util.PaginationUtils;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import com.alisimsek.taskmanagement.user.service.UserService;
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

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
    }

    @GetMapping("/{guid}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByGuid(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByGuid(guid)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(Pageable pageable) {
        Page<UserDto> usersPage = userService.getAllUsers(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(usersPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(usersPage.getContent()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDto>>> searchUsers(@ModelAttribute UserSearchRequest request, Pageable pageable) {
        Page<UserDto> usersPage = userService.searchUsers(request, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHeaders(usersPage);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(usersPage.getContent()));
    }

    @PutMapping("/{guid}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable String guid, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(guid, request)));
    }

    @DeleteMapping("/{guid}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String guid) {
        userService.deleteUser(guid);
        return ResponseEntity.ok(ApiResponse.empty());
    }

    @PostMapping("/activate/{guid}")
    public ResponseEntity<ApiResponse<UserDto>> activateUser(@PathVariable String guid) {
        return ResponseEntity.ok(ApiResponse.success(userService.activateUser(guid)));
    }

    @PostMapping("/{userGuid}/add-role/{userRoleGuid}")
    public ResponseEntity<ApiResponse<UserDto>> addUserRoleToUser(@PathVariable String userGuid, @PathVariable String userRoleGuid) {
        return ResponseEntity.ok(ApiResponse.success(userService.addUserRoleToUser(userGuid, userRoleGuid)));
    }
}
