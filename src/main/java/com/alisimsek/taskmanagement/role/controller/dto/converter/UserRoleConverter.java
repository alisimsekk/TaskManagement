package com.alisimsek.taskmanagement.role.controller.dto.converter;

import com.alisimsek.taskmanagement.common.BaseConverter;
import com.alisimsek.taskmanagement.role.controller.dto.request.UserRoleCreateRequest;
import com.alisimsek.taskmanagement.role.controller.dto.response.UserRoleDto;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRoleConverter extends BaseConverter<UserRole, UserRoleDto> {
    @Override
    public UserRoleDto convert(UserRole source) {
        return UserRoleDto.builder()
                .guid(source.getGuid())
                .entityStatus(source.getEntityStatus())
                .name(source.getName())
                .description(source.getDescription())
                .userPermissions(source.getUserPermissions())
                .build();
    }

    public UserRole convertToEntity(UserRoleCreateRequest request) {
        UserRole userRole = new UserRole();
        userRole.setName(request.getName());
        userRole.setDescription(request.getDescription());
        userRole.setUserPermissions(request.getUserPermissions());
        return userRole;
    }
}
