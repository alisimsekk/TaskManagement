package com.alisimsek.taskmanagement.comment.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateRequest {
    @NotBlank
    private String content;
    @NotBlank
    private String taskGuid;
}
