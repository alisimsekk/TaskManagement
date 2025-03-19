package com.alisimsek.taskmanagement.attachment.service;

import com.alisimsek.taskmanagement.attachment.entity.QAttachment;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttachmentQueryBuilder {
    public static BooleanBuilder createQuery(String originalFilename, String taskTitle, EntityStatus entityStatus) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(originalFilename)) {
            builder.and(QAttachment.attachment.originalFilename.containsIgnoreCase(originalFilename));
        }
        if (Objects.nonNull(taskTitle)) {
            builder.and(QAttachment.attachment.task.title.containsIgnoreCase(taskTitle));
        }
        if (Objects.nonNull(entityStatus)) {
            builder.and(QAttachment.attachment.entityStatus.eq(entityStatus));
        }
        return builder;
    }
}
