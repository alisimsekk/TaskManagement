package com.alisimsek.taskmanagement.department.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentCreateRequest {
    @NotBlank
    private String name;
}
