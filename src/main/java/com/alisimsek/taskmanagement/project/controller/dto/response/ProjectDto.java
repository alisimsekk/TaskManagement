package com.alisimsek.taskmanagement.project.controller.dto.response;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.project.entity.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDto {

    private String guid;
    private String title;
    private EntityStatus entityStatus;
    private ProjectStatus status;
    private DepartmentDto department;
    private List<ProjectTeamMemberDto> teamMembers;
}
