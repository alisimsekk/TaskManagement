package com.alisimsek.taskmanagement.role;

import com.alisimsek.taskmanagement.role.controller.dto.request.UserRoleCreateRequest;
import com.alisimsek.taskmanagement.role.controller.dto.response.UserRoleDto;
import com.alisimsek.taskmanagement.role.entity.UserPermission;
import com.alisimsek.taskmanagement.role.entity.UserRole;

import java.util.Set;

public class UserRoleTestProvider {

    public static String ROLE_GUID = "user-role-guid";
    public static String ROLE_NAME = "user-role-name";
    public static String ROLE_DESCRIPTION = "user-role-description";
    public static String ROLE_BASE_URI = "/api/v1/roles";

    public static UserRoleCreateRequest getUserRoleCreateRequest() {
        UserRoleCreateRequest request = new UserRoleCreateRequest();
        request.setName(ROLE_NAME);
        request.setDescription(ROLE_DESCRIPTION);
        request.setUserPermissions(Set.of(UserPermission.ADD_ATTACHMENT));
        return request;
    }

    public static UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setGuid(ROLE_GUID);
        userRole.setName(ROLE_NAME);
        userRole.setDescription(ROLE_DESCRIPTION);
        userRole.setUserPermissions(Set.of(UserPermission.ADD_ATTACHMENT));
        return userRole;
    }

    public static UserRoleDto getUserRoleDto() {
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setGuid(ROLE_GUID);
        userRoleDto.setName(ROLE_NAME);
        userRoleDto.setDescription(ROLE_DESCRIPTION);
        userRoleDto.setUserPermissions(Set.of(UserPermission.ADD_ATTACHMENT));
        return userRoleDto;
    }
}
