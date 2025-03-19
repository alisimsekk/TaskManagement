package com.alisimsek.taskmanagement.project.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddDepartmentToProjectRequest {
    @NotBlank
    private String projectGuid;
    @NotBlank
    private String departmentGuid;
}
