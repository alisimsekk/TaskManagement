package com.alisimsek.taskmanagement.department.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.alisimsek.taskmanagement.department.entity.QDepartment;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentQueryBuilder {

    public static BooleanBuilder createQuery(String departmentName, EntityStatus entityStatus) {
        BooleanBuilder builder = new BooleanBuilder();

        if (Objects.nonNull(departmentName)) {
            builder.and(QDepartment.department.name.containsIgnoreCase(departmentName));
        }
        if (Objects.nonNull(entityStatus)) {
            builder.and(QDepartment.department.entityStatus.eq(entityStatus));
        }
        return builder;
    }
}
