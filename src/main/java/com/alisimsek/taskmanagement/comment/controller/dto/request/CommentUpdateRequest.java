package com.alisimsek.taskmanagement.comment.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateRequest {
    @NotBlank
    private String content;
}
