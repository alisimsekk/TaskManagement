package com.alisimsek.taskmanagement.user.service;

import com.alisimsek.taskmanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.taskmanagement.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQueryBuilder {
    public static BooleanBuilder createQuery(UserSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(request.getUsername())){
            builder.and(QUser.user.username.containsIgnoreCase(request.getUsername()));
        }
        if (Objects.nonNull(request.getFirstName())){
            builder.and(QUser.user.firstName.containsIgnoreCase(request.getFirstName()));
        }
        if (Objects.nonNull(request.getLastName())){
            builder.and(QUser.user.lastName.containsIgnoreCase(request.getLastName()));
        }
        if (Objects.nonNull(request.getUserType())) {
            builder.and(QUser.user.userType.eq(request.getUserType()));
        }
        if (Objects.nonNull(request.getEntityStatus())) {
            builder.and(QUser.user.entityStatus.eq(request.getEntityStatus()));
        }
        return builder;
    }
}
