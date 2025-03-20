package com.alisimsek.taskmanagement.department;

import com.alisimsek.taskmanagement.department.controller.dto.request.DepartmentCreateRequest;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.entity.Department;

public class DepartmentTestProvider {

    public static final String DEPARTMENT_NAME = "Java Backend Team";
    public static final String DEPARTMENT_API_BASE_PATH = "/api/v1/departments";

    public static DepartmentCreateRequest getDepartmentCreateRequest() {
        return DepartmentCreateRequest.builder()
                .name(DEPARTMENT_NAME)
                .build();
    }

    public static DepartmentDto getDepartmentDto() {
        return DepartmentDto.builder()
                .name(DEPARTMENT_NAME)
                .build();
    }

    public static Department getDepartment() {
        return Department.create(DEPARTMENT_NAME);
    }
}
