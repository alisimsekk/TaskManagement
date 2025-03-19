package com.alisimsek.taskmanagement.project.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.entity.ProjectStatus;
import com.alisimsek.taskmanagement.project.entity.QProject;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectQueryBuilder {

    public static BooleanBuilder createQuery(String title, ProjectStatus status, String departmentName, EntityStatus entityStatus) {
        BooleanBuilder builder = new BooleanBuilder();

        if (Objects.nonNull(title)) {
            builder.and(QProject.project.title.containsIgnoreCase(title));
        }
        if (Objects.nonNull(status)) {
            builder.and(QProject.project.status.eq(status));
        }
        if (Objects.nonNull(departmentName)) {
            builder.and(QProject.project.department.name.containsIgnoreCase(departmentName));
        }
        if (Objects.nonNull(entityStatus)) {
            builder.and(QProject.project.entityStatus.eq(entityStatus));
        }
        return builder;
    }
}
