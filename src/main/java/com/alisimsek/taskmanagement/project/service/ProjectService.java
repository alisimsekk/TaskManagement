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
import com.alisimsek.taskmanagement.user.entity.UserType;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectConverter projectConverter;
    private final UserRepository userRepository;

    public ProjectDto createProject(ProjectCreateRequest request) {
        Optional<Project> departmentFromDb = projectRepository.findByTitleIgnoreCaseAndEntityStatus(request.getTitle(), EntityStatus.ACTIVE);
        if (departmentFromDb.isPresent()) {
            throw new EntityAlreadyExistsException();
        }
        Project newProject = projectConverter.convertToEntity(request);
        return projectConverter.convert(projectRepository.save(newProject));
    }

    public ProjectDto getProjectByGuid(String guid) {
        return projectConverter.convert(projectRepository.getByGuid(guid));
    }

    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        Page<Project> projectsPage = projectRepository.findAll(pageable);
        return projectsPage.map(projectConverter::convert);
    }

    public Page<ProjectDto> searchProjects(String title, ProjectStatus status, String departmentName, EntityStatus entityStatus, Pageable pageable) {
        BooleanBuilder builder = ProjectQueryBuilder.createQuery(title, status, departmentName, entityStatus);
        Page<Project> projectsPage = projectRepository.findAll(builder, pageable);
        return projectsPage.map(projectConverter::convert);
    }

    public ProjectDto updateProject(String guid, ProjectCreateRequest request) {
        if (projectRepository.existsByTitleAndStatusExceptGuid(request.getTitle(), EntityStatus.ACTIVE, guid)) {
            throw new EntityAlreadyExistsException();
        }
        Project projectFromDb = projectRepository.getByGuid(guid);
        projectFromDb.setTitle(request.getTitle());
        projectFromDb.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(projectFromDb);
        log.info("Updated project -> {}", updatedProject.getTitle());
        return projectConverter.convert(updatedProject);
    }

    public void deleteProject(String guid) {
        Project projectFromDb = projectRepository.getByGuid(guid);
        projectRepository.delete(projectFromDb);
        log.info("Deleted project -> {}", projectFromDb.getTitle());
    }

    public ProjectDto activateProject(String guid) {
        Project projectFromDb = projectRepository.getByGuid(guid);
        projectRepository.activate(projectFromDb);
        log.info("Activated project -> {}", projectFromDb.getTitle());
        return projectConverter.convert(projectFromDb);
    }

    public ProjectDto addDepartmentToProject(AddDepartmentToProjectRequest request) {
        Department department = departmentRepository.getByGuid(request.getDepartmentGuid());
        Project project = projectRepository.getByGuid(request.getProjectGuid());
        project.setDepartment(department);
        Project savedProject = projectRepository.save(project);
        log.info("{} department added to {} project", department.getName(), project.getTitle());
        return projectConverter.convert(savedProject);
    }

    public ProjectDto addTeamMemberToProject(AddTeamMemberToProjectRequest request) {
        User user = userRepository.getByGuid(request.getTeamMemberGuid());
        if (!UserType.TEAM_MEMBER.equals(user.getUserType())) {
            throw new InvalidUserTypeException();
        }
        Project project = projectRepository.getByGuid(request.getProjectGuid());
        project.getTeamMembers().add(user);
        Project savedProject = projectRepository.save(project);
        log.info("{} team member added to {} project", user.getUsername(), project.getTitle());
        return projectConverter.convert(savedProject);
    }
}
