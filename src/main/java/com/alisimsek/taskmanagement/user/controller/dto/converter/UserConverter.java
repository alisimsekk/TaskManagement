package com.alisimsek.taskmanagement.user.controller.dto.converter;

import com.alisimsek.taskmanagement.common.BaseConverter;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import com.alisimsek.taskmanagement.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter extends BaseConverter<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        return UserDto.builder()
                .guid(source.getGuid())
                .entityStatus(source.getEntityStatus())
                .username(source.getUsername())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .userType(source.getUserType())
                .userPermissions(source.getUserPermissions())
                .build();
    }

    public User convertToEntity(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserType(request.getUserType());
        return user;
    }
}
