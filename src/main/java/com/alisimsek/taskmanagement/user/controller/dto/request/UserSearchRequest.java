package com.alisimsek.taskmanagement.user.controller.dto.request;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.user.entity.UserType;
import lombok.Data;

@Data
public class UserSearchRequest {
    private String username;
    private String firstName;
    private String lastName;
    private UserType userType;
    private EntityStatus entityStatus = EntityStatus.ACTIVE;
}
