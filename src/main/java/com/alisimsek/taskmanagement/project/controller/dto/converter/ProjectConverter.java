package com.alisimsek.taskmanagement.project.controller.dto.converter;

import com.alisimsek.taskmanagement.common.BaseConverter;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.department.controller.dto.converter.DepartmentConverter;
import com.alisimsek.taskmanagement.project.controller.dto.request.ProjectCreateRequest;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectDto;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectTeamMemberDto;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectConverter extends BaseConverter<Project, ProjectDto> {

    private final DepartmentConverter departmentConverter;

    @Override
    public ProjectDto convert(Project source) {
        return ProjectDto.builder()
                .guid(source.getGuid())
                .title(source.getTitle())
                .entityStatus(source.getEntityStatus())
                .status(source.getStatus())
                .department((Objects.nonNull(source.getDepartment()) && EntityStatus.ACTIVE.equals(source.getDepartment().getEntityStatus())) ?
                        departmentConverter.convert(source.getDepartment()) : null)
                .teamMembers(source.getTeamMembers().stream().
                        filter(teamMember -> EntityStatus.ACTIVE.equals(teamMember.getEntityStatus()))
                        .map(this::convertToTeamMemberDto).collect(Collectors.toList()))
                .build();
    }

    public Project convertToEntity(ProjectCreateRequest request) {
        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        return project;
    }

    private ProjectTeamMemberDto convertToTeamMemberDto(User teamMember) {
        return ProjectTeamMemberDto.builder()
                .guid(teamMember.getGuid())
                .username(teamMember.getUsername())
                .build();
    }
}
