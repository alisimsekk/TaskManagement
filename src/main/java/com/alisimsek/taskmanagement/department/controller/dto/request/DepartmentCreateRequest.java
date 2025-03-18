package com.alisimsek.taskmanagement.department.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentCreateRequest {
    @NotBlank
    private String name;
}
