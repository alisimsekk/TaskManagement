package com.alisimsek.taskmanagement.task.service;

import com.alisimsek.taskmanagement.task.controller.dto.request.TaskSearchRequest;
import com.alisimsek.taskmanagement.task.entity.QTask;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskQueryBuilder {

    public static BooleanBuilder createQuery(TaskSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(request.getTitle())) {
            builder.and(QTask.task.title.containsIgnoreCase(request.getTitle()));
        }
        if (Objects.nonNull(request.getState())) {
            builder.and(QTask.task.state.eq(request.getState()));
        }
        if (Objects.nonNull(request.getPriority())) {
            builder.and(QTask.task.priority.eq(request.getPriority()));
        }
        if (Objects.nonNull(request.getAssigneeUserGuid())) {
            builder.and(QTask.task.assignee.guid.eq(request.getAssigneeUserGuid()));
        }
        if (Objects.nonNull(request.getProjectGuid())) {
            builder.and(QTask.task.project.guid.eq(request.getProjectGuid()));
        }
        if (Objects.nonNull(request.getProjectTitle())) {
            builder.and(QTask.task.project.title.containsIgnoreCase(request.getProjectTitle()));
        }
        if (Objects.nonNull(request.getEntityStatus())) {
            builder.and(QTask.task.entityStatus.eq(request.getEntityStatus()));
        }
        return builder;
    }
}
