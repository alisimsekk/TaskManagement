package com.alisimsek.taskmanagement.user.controller.dto.response;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.role.entity.UserPermission;
import com.alisimsek.taskmanagement.user.entity.UserType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDto {
    private String guid;
    private EntityStatus entityStatus;
    private String username;
    private String firstName;
    private String lastName;
    private UserType userType;
    private Set<UserPermission> userPermissions;
}
