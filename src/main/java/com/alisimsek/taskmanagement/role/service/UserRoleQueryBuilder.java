package com.alisimsek.taskmanagement.role.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.role.entity.QUserRole;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRoleQueryBuilder {
    public static BooleanBuilder createQuery(String name, EntityStatus entityStatus) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(name)) {
            builder.and(QUserRole.userRole.name.containsIgnoreCase(name));
        }
        if (Objects.nonNull(entityStatus)) {
            builder.and(QUserRole.userRole.entityStatus.eq(entityStatus));
        }
        return builder;
    }
}
