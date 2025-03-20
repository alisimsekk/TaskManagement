package com.alisimsek.taskmanagement.project;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.department.entity.Department;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddDepartmentToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddTeamMemberToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.ProjectCreateRequest;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectDto;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectTeamMemberDto;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.project.entity.ProjectStatus;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.entity.UserType;

import java.util.Collections;

public class ProjectTestProvider {

    public static String PROJECT_GUID = "project-guid";
    public static String PROJECT_TITLE = "Test Project";
    public static String PROJECT_DESCRIPTION = "Test Description";
    public static String DEPARTMENT_GUID = "department-guid";
    public static String DEPARTMENT_NAME = "IT Department";
    public static String USER_GUID = "user-guid";
    public static String UPDATED_TITLE = "updated title";
    public static String PROJECT_BASE_URI = "/api/v1/projects";

    public static Project getProject() {
        Project project = new Project();
        project.setId(1L);
        project.setGuid(PROJECT_GUID);
        project.setTitle(PROJECT_TITLE);
        project.setDescription(PROJECT_DESCRIPTION);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setEntityStatus(EntityStatus.ACTIVE);
        project.setDepartment(null);
        return project;
    }

    public static ProjectDto getProjectDto() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setGuid(PROJECT_GUID);
        projectDto.setTitle(PROJECT_TITLE);
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        projectDto.setTeamMembers(Collections.singletonList(getProjectTeamMemberDto()));
        projectDto.setEntityStatus(EntityStatus.ACTIVE);
        return projectDto;
    }

    public static ProjectCreateRequest getProjectCreateRequest() {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle(PROJECT_TITLE);
        request.setDescription(PROJECT_DESCRIPTION);
        return request;
    }

    public static Department getDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setGuid(DEPARTMENT_GUID);
        department.setName(DEPARTMENT_NAME);
        return department;
    }

    public static User getTeamMember() {
        User user = new User();
        user.setId(1L);
        user.setGuid(USER_GUID);
        user.setUsername("team-member");
        user.setUserType(UserType.TEAM_MEMBER);
        return user;
    }

    public static User getNonTeamMemberUser() {
        User user = new User();
        user.setId(2L);
        user.setGuid("non-team-member-guid");
        user.setUsername("non-team-member");
        user.setUserType(UserType.ADMIN);
        return user;
    }

    public static AddDepartmentToProjectRequest getAddDepartmentToProjectRequest() {
        AddDepartmentToProjectRequest request =  new AddDepartmentToProjectRequest();
        request.setProjectGuid(PROJECT_GUID);
        request.setDepartmentGuid(DEPARTMENT_GUID);
        return request;
    }

    public static AddTeamMemberToProjectRequest getAddTeamMemberToProjectRequest() {
        AddTeamMemberToProjectRequest request = new AddTeamMemberToProjectRequest();
        request.setProjectGuid(PROJECT_GUID);
        request.setTeamMemberGuid(USER_GUID);
        return request;
    }

    public static ProjectTeamMemberDto getProjectTeamMemberDto() {
        return ProjectTeamMemberDto.builder()
                .guid(USER_GUID)
                .username("team-member")
                .build();
    }
}
