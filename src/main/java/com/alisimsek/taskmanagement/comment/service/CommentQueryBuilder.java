package com.alisimsek.taskmanagement.comment.service;

import com.alisimsek.taskmanagement.comment.entity.QComment;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentQueryBuilder {

    public static BooleanBuilder createQuery(String taskTitle, String authorFirstName, String authorLastName, EntityStatus entityStatus) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(taskTitle)) {
            builder.and(QComment.comment.task.title.containsIgnoreCase(taskTitle));
        }
        if (Objects.nonNull(authorFirstName)) {
            builder.and(QComment.comment.author.firstName.containsIgnoreCase(authorFirstName));
        }
        if (Objects.nonNull(authorLastName)) {
            builder.and(QComment.comment.author.lastName.containsIgnoreCase(authorLastName));
        }
        if (Objects.nonNull(entityStatus)) {
            builder.and(QComment.comment.entityStatus.eq(entityStatus));
        }
        return builder;
    }
}
