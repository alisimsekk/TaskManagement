package com.alisimsek.taskmanagement.project.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.common.exception.InvalidUserTypeException;
import com.alisimsek.taskmanagement.department.entity.Department;
import com.alisimsek.taskmanagement.department.repository.DepartmentRepository;
import com.alisimsek.taskmanagement.project.controller.dto.converter.ProjectConverter;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddDepartmentToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddTeamMemberToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.ProjectCreateRequest;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectDto;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.project.entity.ProjectStatus;
import com.alisimsek.taskmanagement.project.repository.ProjectRepository;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.alisimsek.taskmanagement.project.ProjectTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectConverter projectConverter;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;
    private ProjectCreateRequest projectCreateRequest;
    private Department department;
    private User teamMember;
    private User nonTeamMember;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        project = getProject();
        projectDto = getProjectDto();
        projectCreateRequest = getProjectCreateRequest();
        department = getDepartment();
        teamMember = getTeamMember();
        nonTeamMember = getNonTeamMemberUser();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createProject_WhenProjectDoesNotExist_ShouldReturnProjectDto() {
        when(projectRepository.findByTitleIgnoreCaseAndEntityStatus(anyString(), any(EntityStatus.class)))
                .thenReturn(Optional.empty());
        when(projectConverter.convertToEntity(any(ProjectCreateRequest.class))).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.createProject(projectCreateRequest);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(projectRepository).findByTitleIgnoreCaseAndEntityStatus(projectCreateRequest.getTitle(), EntityStatus.ACTIVE);
    }

    @Test
    void createProject_WhenProjectExists_ShouldThrowEntityAlreadyExistsException() {
        when(projectRepository.findByTitleIgnoreCaseAndEntityStatus(anyString(), any(EntityStatus.class)))
                .thenReturn(Optional.of(project));

        assertThrows(EntityAlreadyExistsException.class, () -> projectService.createProject(projectCreateRequest));
      verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectByGuid_ShouldReturnProjectDto() {
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.getProjectByGuid(PROJECT_GUID);

        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void getAllProjects_ShouldReturnPageOfProjectDto() {
        List<Project> projects = List.of(project);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, projects.size());

        when(projectRepository.findAll(any(Pageable.class))).thenReturn(projectPage);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        Page<ProjectDto> result = projectService.getAllProjects(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(projectDto, result.getContent().getFirst());
    }

    @Test
    void searchProjects_ShouldReturnPageOfProjectDto() {
        ProjectStatus status = ProjectStatus.IN_PROGRESS;
        EntityStatus entityStatus = EntityStatus.ACTIVE;
        List<Project> projects = List.of(project);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, projects.size());


        when(projectRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(projectPage);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        Page<ProjectDto> result = projectService.searchProjects(PROJECT_TITLE, status, DEPARTMENT_NAME, entityStatus, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(projectDto, result.getContent().getFirst());
    }

    @Test
    void updateProject_WhenProjectTitleDoesNotExist_ShouldReturnUpdatedProjectDto() {
        projectCreateRequest.setTitle(UPDATED_TITLE);
        projectDto.setTitle(UPDATED_TITLE);

        when(projectRepository.existsByTitleAndStatusExceptGuid(anyString(), any(EntityStatus.class), anyString()))
                .thenReturn(false);
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.updateProject(PROJECT_GUID, projectCreateRequest);

        assertNotNull(result);
        assertEquals(projectDto, result);
        assertEquals(UPDATED_TITLE, result.getTitle());
        verify(projectRepository).getByGuid(PROJECT_GUID);
    }

    @Test
    void updateProject_WhenProjectTitleExists_ShouldThrowEntityAlreadyExistsException() {
        projectCreateRequest.setTitle(UPDATED_TITLE);
        when(projectRepository.existsByTitleAndStatusExceptGuid(anyString(), any(EntityStatus.class), anyString()))
                .thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> projectService.updateProject(PROJECT_GUID, projectCreateRequest));
    }

    @Test
    void deleteProject_ShouldDeleteProject() {
        when(projectRepository.getByGuid(anyString())).thenReturn(project);

        projectService.deleteProject(PROJECT_GUID);

        verify(projectRepository).delete(project);
    }

    @Test
    void activateProject_ShouldActivateProject() {
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.activateProject(PROJECT_GUID);

        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void addDepartmentToProject_ShouldAddDepartmentToProject() {
        AddDepartmentToProjectRequest request = getAddDepartmentToProjectRequest();

        when(departmentRepository.getByGuid(anyString())).thenReturn(department);
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.addDepartmentToProject(request);

        assertNotNull(result);
        assertEquals(projectDto, result);
        verify(departmentRepository).getByGuid(request.getDepartmentGuid());
        verify(projectRepository).getByGuid(request.getProjectGuid());
    }

    @Test
    void addTeamMemberToProject_WhenUserIsTeamMember_ShouldAddTeamMemberToProject() {
        AddTeamMemberToProjectRequest request = getAddTeamMemberToProjectRequest();

        when(userRepository.getByGuid(anyString())).thenReturn(teamMember);
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectConverter.convert(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.addTeamMemberToProject(request);

        assertNotNull(result);
        assertEquals(projectDto, result);
        assertEquals(teamMember.getGuid(), result.getTeamMembers().getFirst().getGuid());
        verify(userRepository).getByGuid(request.getTeamMemberGuid());
        verify(projectRepository).getByGuid(request.getProjectGuid());
    }

    @Test
    void addTeamMemberToProject_WhenUserIsNotTeamMember_ShouldThrowInvalidUserTypeException() {
        AddTeamMemberToProjectRequest request = getAddTeamMemberToProjectRequest();
        request.setTeamMemberGuid(nonTeamMember.getGuid());

        when(userRepository.getByGuid(anyString())).thenReturn(nonTeamMember);

        assertThrows(InvalidUserTypeException.class, () -> projectService.addTeamMemberToProject(request));
    }
}
