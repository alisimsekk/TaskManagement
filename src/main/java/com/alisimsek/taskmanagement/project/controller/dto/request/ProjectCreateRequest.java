package com.alisimsek.taskmanagement.project.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCreateRequest {

    @NotBlank
    private String title;
    private String description;
}
