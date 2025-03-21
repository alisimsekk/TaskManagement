package com.alisimsek.taskmanagement.user;

import com.alisimsek.taskmanagement.role.entity.UserPermission;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.entity.UserType;

import java.util.Collections;

public class UserTestProvider {
    public static String USER_GUID = "test-user-guid";
    public static String USER_ROLE_GUID = "test-role-guid";
    public static String USERNAME = "test@example.com";
    public static String USER_BASE_URI = "/api/v1/users";

    public static User getTestUser() {
        User user = new User();
        user.setGuid(USER_GUID);
        user.setUsername(USERNAME);
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserType(UserType.PROJECT_MANAGER);
        return user;
    }

    public static UserCreateRequest getTestUserCreateRequest() {
        return UserCreateRequest.builder()
                .username(USERNAME)
                .password("Test123456")
                .firstName("Test")
                .lastName("User")
                .userType(UserType.PROJECT_MANAGER)
                .build();
    }

    public static UserUpdateRequest getTestUserUpdateRequest() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("NewPassword123");
        request.setFirstName("Updated");
        request.setLastName("User");
        return request;
    }

    public static UserRole getTestUserRole() {
        UserRole userRole = new UserRole();
        userRole.setGuid(USER_ROLE_GUID);
        userRole.setName("super-pm-role");
        userRole.setUserPermissions(Collections.singleton(UserPermission.UPDATE_TASK_STATE));
        return userRole;
    }

    public static UserDto getTestUserDto() {
        return UserDto.builder()
                .guid(USER_GUID)
                .username(USERNAME)
                .firstName("Test")
                .lastName("User")
                .userType(UserType.PROJECT_MANAGER)
                .build();
    }
}
