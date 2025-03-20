package com.alisimsek.taskmanagement.role.controller.dto.response;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.role.entity.UserPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDto {
    private String guid;
    private EntityStatus entityStatus;
    private String name;
    private String description;
    private Set<UserPermission> userPermissions;
}
