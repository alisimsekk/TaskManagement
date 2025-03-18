package com.alisimsek.taskmanagement.department.controller.dto.converter;

import com.alisimsek.taskmanagement.common.BaseConverter;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentConverter extends BaseConverter<Department, DepartmentDto> {
    @Override
    public DepartmentDto convert(Department source) {
        return DepartmentDto.builder()
                .guid(source.getGuid())
                .name(source.getName())
                .entityStatus(source.getEntityStatus())
                .build();
    }
}
