package com.alisimsek.taskmanagement.comment.controller.dto.response;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
    private String guid;
    private String content;
    private String taskGuid;
    private String authorGuid;
    private EntityStatus entityStatus;
}
