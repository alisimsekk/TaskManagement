package com.alisimsek.taskmanagement.role.controller.dto.request;

import com.alisimsek.taskmanagement.role.entity.UserPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class UserRoleCreateRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @NotEmpty
    private Set<UserPermission> userPermissions;
}
