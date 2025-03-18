package com.alisimsek.taskmanagement.department.controller.dto.response;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentDto {
    private String guid;
    private String name;
    private EntityStatus entityStatus;
}
